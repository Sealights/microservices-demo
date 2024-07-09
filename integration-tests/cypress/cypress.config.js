const { defineConfig } = require('cypress');
const { registerSealightsTasks } = require('sealights-cypress-plugin');

module.exports = defineConfig({
  e2e: {
    experimentalInteractiveRunEvents: true,
    testIsolation: false,
    async setupNodeEvents(on, config) {
      await registerSealightsTasks(on, config);
     },
     specPattern: 'cypress/integration/**/*.js', // Ensure this includes your spec files
     supportFile: 'cypress/support/index.js', // Update if you prefer using index.js
  },
   component: {
      specPattern: '**/*.component.js',
      supportFile: 'cypress/support/component.js', // Example for component testing
    },
});
