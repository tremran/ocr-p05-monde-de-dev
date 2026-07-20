Cypress.Commands.add('loginViaApi', (email, password, path = '/feed') => {
  cy.request('POST', `${Cypress.env('apiUrl')}/auth/login`, { email, password }).then(({ body }) => {
    const token = body.token || body.data?.token;
    expect(token, 'JWT token from login response').to.be.a('string').and.not.be.empty;

    cy.visit(path, {
      onBeforeLoad(win) {
        win.localStorage.setItem('auth_token', token);
      },
    });
  });
});

Cypress.Commands.add('authHeaders', (email, password) => {
  return cy
    .request('POST', `${Cypress.env('apiUrl')}/auth/login`, { email, password })
    .then(({ body }) => {
      const token = body.token || body.data?.token;
      expect(token, 'JWT token from login response').to.be.a('string').and.not.be.empty;
      return { Authorization: `Bearer ${token}` };
    });
});
