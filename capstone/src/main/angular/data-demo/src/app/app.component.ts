import { Component } from '@angular/core';
import { OAuthService, JwksValidationHandler } from 'angular-oauth2-oidc';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'data-demo';

    // constructor(private oauthService: OAuthService) {
    //   this.oauthService.redirectUri = window.location.origin;
    //   this.oauthService.clientId = '{clientId}';
    //   this.oauthService.scope = 'openid profile email';
    //   this.oauthService.issuer = 'https://{yourOktaDomain}/oauth2/default';
    //   this.oauthService.tokenValidationHandler = new JwksValidationHandler();
    //
    //   // Load Discovery Document and then try to login the user
    //   this.oauthService.loadDiscoveryDocumentAndTryLogin();
    // }
}
