const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    specPattern: 'cypress/e2e/**/*.cy.js',
    supportFile: 'cypress/support/e2e.js',
    baseUrl: 'http://localhost:4200',
    env: {
      apiUrl: 'http://localhost:3001/api/v1',
    },
  },
  video: false,
  screenshotOnRunFailure: true,
  viewportWidth: 1366,
  viewportHeight: 768,
});
