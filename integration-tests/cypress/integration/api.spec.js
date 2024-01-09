describe('Api Tests', () => {
  it('should return 200 for index page', () => {
    cy.request('GET', Cypress.env('machine_dns')).then(response => {
      cy.wrap(response.status).should('equal', 200);
    });
  });

  it('should be able to set different currencies', () => {
    const currencies = ['EUR', 'USD', 'JPY', 'CAD'];
    for (const currency of currencies) {
      cy.request('POST', Cypress.env('machine_dns') + '/setCurrency', { currency_code: currency }).then(response => {
        cy.wrap(response.status).should('equal', 200);
      });
    }
  });

  it('should return 200 for browsing products', () => {
    const products = [
      '0PUK6V6EV0',
      '1YMWWN1N4O',
      // ... other product IDs
    ];

    for (const product_id of products) {
      cy.request('GET', `${Cypress.env('machine_dns')}/product/${product_id}`).then(response => {
        cy.wrap(response.status).should('equal', 200);
      });
    }
  });

  it('should return 404 for a non-existent route', () => {
    cy.request('GET', Cypress.env('machine_dns') + '/nonexistent-route', null, { failOnStatusCode: false }).then(response => {
      cy.wrap(response.status).should('equal', 404);
    });
  });

 it('should return 200 for invalid request data', () => {
   cy.request({method: 'POST', url: Cypress.env('machine_dns') + '/setCurrency', body: { invalid_key: 'invalid_value' }, failOnStatusCode: false }).then(response => {
     cy.wrap(response.status).should('equal', 200);
   });
 });


});