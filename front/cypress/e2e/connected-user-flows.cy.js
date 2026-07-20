describe('Parcours utilisateur connecté', () => {
  const stamp = Date.now();
  const user = {
    email: `cypress-${stamp}@test.com`,
    pseudo: `cypress-user-${stamp}`,
    password: 'StrongPass123!',
  };

  let firstTopicId;
  let createdPostId;
  const createdPostTitle = `Article Cypress ${stamp}`;

  const todayIsoDate = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  before(() => {
    cy.request('POST', `${Cypress.env('apiUrl')}/auth/register`, {
      email: user.email,
      pseudo: user.pseudo,
      password: user.password,
    }).its('status').should('eq', 200);

    cy.authHeaders(user.email, user.password).then((headers) => {
      cy.request({
        method: 'GET',
        url: `${Cypress.env('apiUrl')}/topic`,
        headers,
      }).then(({ body }) => {
        expect(body).to.be.an('array').and.have.length.greaterThan(0);
        firstTopicId = body[0].id;
      });
    });
  });

  it('peut consulter son profil', () => {
    cy.loginViaApi(user.email, user.password, '/me');

    cy.contains('h1', 'Profil utilisateur').should('be.visible');
    cy.get('#email').should('have.value', user.email);
    cy.get('#pseudo').should('have.value', user.pseudo);
  });

  it('peut modifier son profil', () => {
    const updatedPseudo = `${user.pseudo}-maj`;
    user.pseudo = updatedPseudo;

    cy.loginViaApi(user.email, user.password, '/me');

    cy.get('#pseudo').clear().type(updatedPseudo);
    cy.contains('button', 'Sauvegarder').click();

    cy.contains('Vos informations ont été mises à jour.').should('be.visible');
    cy.reload();
    cy.get('#pseudo').should('have.value', updatedPseudo);
  });

  it('peut consulter les thèmes', () => {
    cy.loginViaApi(user.email, user.password, '/themes');

    cy.contains('h1', 'Thèmes').should('be.visible');
    cy.get('.topic-card').its('length').should('be.greaterThan', 0);
  });

  it('peut s\'abonner à un thème', () => {
    cy.loginViaApi(user.email, user.password, '/themes');

    cy.contains('.topic-card', /.+/).first().within(() => {
      cy.contains('button', "S'abonner").click();
      cy.contains('button', 'Déjà abonné').should('exist');
    });
  });

  it('peut se désabonner d\'un thème', () => {
    cy.loginViaApi(user.email, user.password, '/me');

    cy.contains('h2', 'Abonnements').should('be.visible');
    cy.contains('Chargement de vos themes...', { timeout: 10000 }).should('not.exist');

    cy.get('.subscribed-topics').then(($section) => {
      const buttonCount = $section.find('.unsubscribe-button button').length;

      if (buttonCount > 0) {
        cy.get('.unsubscribe-button button').then(($buttons) => {
          const initialCount = $buttons.length;

          cy.wrap($buttons[0]).click();

          if (initialCount === 1) {
            cy.contains("Vous n'êtes abonné a aucun thème pour le moment.").should('be.visible');
          } else {
            cy.get('.unsubscribe-button button').should('have.length', initialCount - 1);
          }
        });
      } else {
        cy.contains("Vous n'êtes abonné a aucun thème pour le moment.").should('be.visible');
      }
    });
  });

  it('peut consulter son fil d\'actualité', () => {
    cy.authHeaders(user.email, user.password).then((headers) => {
      cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/subscription/${firstTopicId}`,
        headers,
        failOnStatusCode: false,
      });
    });

    cy.loginViaApi(user.email, user.password, '/feed');
    cy.contains('Créer un article').should('be.visible');
    cy.get('body').should('contain.text', 'Trier par');
  });

  it('peut trier le fil d\'actualité', () => {
    const oldTitle = `Old ${stamp}`;
    const newTitle = `New ${stamp}`;

    cy.authHeaders(user.email, user.password).then((headers) => {
      cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/post`,
        headers,
        body: {
          topicId: firstTopicId,
          title: oldTitle,
          content: 'old content',
          publishedAt: '2026-01-01',
        },
      });

      cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/post`,
        headers,
        body: {
          topicId: firstTopicId,
          title: newTitle,
          content: 'new content',
          publishedAt: '2026-12-31',
        },
      });
    });

    cy.loginViaApi(user.email, user.password, '/feed');

    cy.contains('.post-card h2', newTitle).should('be.visible');
    cy.contains('.post-card h2', oldTitle).should('be.visible');

    cy.intercept('GET', '**/api/v1/feed?sort=ASC').as('feedAsc');
    cy.get('.sort-toggle').click();
    cy.wait('@feedAsc').its('response.statusCode').should('eq', 200);
    cy.get('.sort-arrow').should('contain.text', '↑');

    cy.get('.post-card h2').then(($titles) => {
      const values = [...$titles].map((el) => el.textContent?.trim());
      expect(values.indexOf(oldTitle)).to.be.greaterThan(-1);
      expect(values.indexOf(newTitle)).to.be.greaterThan(-1);
      expect(values.indexOf(oldTitle)).to.be.lessThan(values.indexOf(newTitle));
    });
  });

  it('peut ajouter un article', () => {
    cy.loginViaApi(user.email, user.password, '/article/nouveau');

    cy.get('#topicId').select(`${firstTopicId}`);
    cy.get('#title').type(createdPostTitle);
    cy.get('#content').type('Contenu créé par Cypress');

    cy.intercept('POST', '**/api/v1/post').as('createPost');
    cy.contains('button', 'Créer').click();

    cy.wait('@createPost').then((interception) => {
      expect(interception.response?.statusCode).to.eq(200);
      createdPostId = interception.response?.body?.id;
      expect(createdPostId).to.exist;
      expect(interception.request?.body?.publishedAt).to.eq(todayIsoDate());
    });

    cy.url().should('include', '/feed');
    cy.contains('.post-card h2', createdPostTitle).should('be.visible');
  });

  it('peut consulter un article', () => {
    cy.loginViaApi(user.email, user.password, `/article/${createdPostId}`);

    cy.contains('h1', createdPostTitle).should('be.visible');
    cy.contains('Commentaires').should('be.visible');
  });

  it('peut commenter un article', () => {
    const commentText = `Commentaire Cypress ${stamp}`;

    cy.loginViaApi(user.email, user.password, `/article/${createdPostId}`);

    cy.get('#comment-content').type(commentText);
    cy.contains('button', 'Commenter').click();

    cy.contains('.comment-content', commentText).should('be.visible');
  });
});
