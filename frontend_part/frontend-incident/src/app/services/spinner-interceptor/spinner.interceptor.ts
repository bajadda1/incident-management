import {Injectable} from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {NgxSpinnerService} from 'ngx-spinner';

@Injectable()
export class SpinnerInterceptor implements HttpInterceptor {
  constructor(
    private spinner: NgxSpinnerService) {
  }

  /*
 API calls always go though interceptor, so it shows spinner and then when call returns if hides
 */
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    this.spinner.show();
    return next.handle(req).pipe(
      finalize(() => {
        this.spinner.hide();
      })
    );
  }

}
