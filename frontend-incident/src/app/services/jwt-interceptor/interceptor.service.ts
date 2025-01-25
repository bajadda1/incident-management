import {Injectable} from '@angular/core';
import {HttpInterceptor, HttpRequest, HttpHandler} from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('JWT_TOKEN'); // Retrieve the token from localStorage
    if (token) {
      const clonedReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`, // Add the Authorization header
        },
      });
      console.log('Outgoing request with token:', clonedReq);
      return next.handle(clonedReq);
    } else {
      console.warn('No token found in localStorage!');
    }
    return next.handle(req);
  }
}
