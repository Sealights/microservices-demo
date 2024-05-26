import { Selector } from 'testcafe';

const BASE_URL = process.env.machine_dns;

fixture('Getting Started')
    .page(BASE_URL).skipJsErrors();

test('Check that the home page loads correctly', async t => {
    const element = Selector('.hot-products-row > div:nth-child(1) > h3:nth-child(1)');
    await t
        .expect(element.innerText).eql('Hot Products');
});
test('Test cart functionality', async t => {
    await t
    .click(Selector('.cart-link'))
        .expect(Selector('.empty-cart-section > h3:nth-child(1)').innerText).eql('Your shopping cart is empty!');
});
test('Add item to cart', async t => {
    await t
    .click(Selector('html body main.home div.container-fluid div.row div.col-12.col-lg-8 div.row.hot-products-row.px-xl-6 div.col-md-4.hot-product-card a div.hot-product-card-img-overlay'))
    .click(Selector('.cymbal-button-primary'))    
    .expect(Selector('.col-4 > h3:nth-child(1)').innerText).eql('Cart (1)');
});
