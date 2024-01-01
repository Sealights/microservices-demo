const { defineConfig } = require("cypress");

module.exports = defineConfig({
    e2e: {
        experimentalInteractiveRunEvents: true, // If you want to run with 'npm cypress open' and still report coverage
        async setupNodeEvents(on, config) {
            await registerSealightsTasks(on, config);
        },
    },
});
