describe('Api Tests', () => {
  beforeEach(() => {
    cy.visit(Cypress.env('machine_dns'))
  })
  it('should return 200 for index page', () => {
    cy.request('GET', '/').then(response => {
      cy.wrap(response.status).should('equal', 200);
    });
  });

  it('should be able to set different currencies', () => {
    const currencies = ['EUR', 'USD', 'JPY', 'CAD'];
    for (const currency of currencies) {
      cy.request('POST', '/setCurrency', { currency_code: currency }).then(response => {
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
      cy.request('GET', `/product/${product_id}`).then(response => {
        cy.wrap(response.status).should('equal', 200);
      });
    }
  });

  it('should return 404 for a non-existent route', () => {
    cy.request('GET', '/nonexistent-route', null, { failOnStatusCode: false }).then(response => {
      cy.wrap(response.status).should('equal', 404);
    });
  });

 it('should return 200 for invalid request data', () => {
   cy.request('POST', '/setCurrency', { invalid_key: 'invalid_value' }, { failOnStatusCode: false }).then(response => {
     cy.wrap(response.status).should('equal', 200);
   });
 });


});

// Run the tests automatically in Cypress:
// npm install cypress --save-dev
// npx cypress run --spec "cypress/integration/api.spec.js"