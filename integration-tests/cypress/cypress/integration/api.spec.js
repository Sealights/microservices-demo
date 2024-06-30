describe('Api Tests', () => {
  it('should return 200 for index page', () => {
    cy.apiRequest('GET', '/').then(response => {
      cy.wrap(response.status).should('equal', 200);
    });
    cy.wait(15000);
  });

  it('should be able to set different currencies', () => {
    const currencies = ['EUR', 'USD', 'JPY', 'CAD'];
    for (const currency of currencies) {
      cy.apiRequest('POST', '/setCurrency', { currency_code: currency }).then(response => {
        cy.wrap(response.status).should('equal', 200);
      });
      cy.wait(15000);
    }
  });

  it('should return 200 for browsing products', () => {

    const products = [
      '0PUK6V6EV0',
      '1YMWWN1N4O',
      // ... other product IDs
    ];

    for (const product_id of products) {
      cy.apiRequest('GET', `/product/${product_id}`).then(response => {
        cy.wrap(response.status).should('equal', 200);
      });
      cy.wait(15000);
    }
  });

  it('should return 404 for a non-existent route', () => {

    cy.apiRequest('GET', '/nonexistent-route', null, { failOnStatusCode: false }).then(response => {
      cy.wrap(response.status).should('equal', 404);
    });
    cy.wait(15000);
  });

 it('should return 200 for invalid request data', () => {
  cy.wait(15000);
   cy.request({method: 'POST', url: Cypress.env('machine_dns') + '/setCurrency', body: { invalid_key: 'invalid_value' }, failOnStatusCode: false }).then(response => {
     cy.wrap(response.status).should('equal', 200);
   });
 });

 it('should return 200 for Sealights page', () => {
     cy.wait(15000);
     cy.request('GET', Cypress.env('machine_dns') + '/sealights').then(response => {
       cy.wrap(response.status).should('equal', 200);
     });
   });


});
