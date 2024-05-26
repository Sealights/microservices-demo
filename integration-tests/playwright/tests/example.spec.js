// @ts-check
const { test, expect } = require('@playwright/test');
const SLService = require("../services/sealightsService");

const BASE_URL = process.env.machine_dns || 'none';

let testStartTime;

test.beforeEach(async ({ page }, testInfo) => {
  // Capture and output logs from browser console
  page.on("console", (msg) => console.log(msg.text()));

  const title = testInfo.title;
  await page.evaluate(
    ({ title, testSession }) => {
      const customEvent = new CustomEvent("set:context", {
        detail: {
          baggage: {
            "x-sl-test-name": title,
            "x-sl-test-session-id": testSession,
          },
        },
      });
      window.dispatchEvent(customEvent);
    },
    { title, testSession: process.env.SEALIGHTS_TEST_SESSION_ID }
  );
  testStartTime = Date.now();
});

test.afterEach(async ({ page }, testInfo) => {
  // Delete context after scenario
  await page.evaluate(() => {
    const customEvent = new CustomEvent("delete:context");
    window.dispatchEvent(customEvent);
  });
  // Send test event to Sealights
  const { title, status } = testInfo;
  console.log(
    process.env.SEALIGHTS_TEST_SESSION_ID,
    title,
    testStartTime,
    Date.now(),
    status
  );

  await SLService.sendTestEvent(
    process.env.SEALIGHTS_TEST_SESSION_ID,
    title,
    testStartTime,
    Date.now(),
    status
  );
  testStartTime = undefined;
});


test('Check home page',async({page})=>{
  await page.goto(BASE_URL);

  // Expect a title "to contain" a substring.
  await expect(page).toHaveTitle('Online Boutique');
});

test('Add item to cart and checkout', async ({ page }) => {
  await page.goto(BASE_URL);

await page.click('xpath=/html/body/main/div[2]/div/div[2]/div[1]/div[10]/a');//Get item
await page.click('xpath=/html/body/main/div[1]/div/div[2]/div/form/button');//Add to cart
await expect(page.locator('xpath=/html/body/main/section/div/div[1]/div[1]/div[1]/h3')).toHaveText('Cart (1)');//Check cart
await page.click('xpath=/html/body/main/section/div/div[2]/form/div[10]/div/button');//Checkout
await expect(page.locator('xpath=/html/body/main/section[1]/div[1]/div[1]/h3')).toHaveText('Your order is complete!');
});

test('Check change currency functionality', async ({ page }) => {
  await page.goto(BASE_URL);
  await expect(page.locator('xpath=/html/body/main/div[2]/div/div[2]/div[1]/div[5]/div/div[2]')).toHaveText('$89.99');
  await page.selectOption('select[name="currency_code"]', 'EUR');
  await expect(page.locator('xpath=/html/body/main/div[2]/div/div[2]/div[1]/div[5]/div/div[2]')).toHaveText('€79.60');
  await page.selectOption('select[name="currency_code"]', 'USD');//Change back to USD
});