import { HttpInterceptorFn } from '@angular/common/http';

// URL REAL DO SEU BACKEND NO RENDER
const BACKEND_URL = 'https://api-ecommerce-95r7.onrender.com';

export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  // Se a requisição começar com '/api', adiciona a URL do Render na frente
  if (req.url.startsWith('/api')) {
    const apiReq = req.clone({ url: `${BACKEND_URL}${req.url}` });
    return next(apiReq);
  }
  
  // Caso contrário, segue normalmente
  return next(req);
};
