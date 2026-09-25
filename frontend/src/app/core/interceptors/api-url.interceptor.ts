import { HttpInterceptorFn } from '@angular/common/http';

const BACKEND_URL = 'https://api-ecommerce-95r7.onrender.com';

export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  // RASTREADOR: Isso vai aparecer no console do navegador se o interceptor estiver funcionando
  console.log('🕵️ INTERCEPTOR RODANDO! URL original:', req.url);

  if (req.url.startsWith('/api')) {
    const novaUrl = `${BACKEND_URL}${req.url}`;
    console.log('✅ URL modificada para:', novaUrl);
    
    const apiReq = req.clone({ url: novaUrl });
    return next(apiReq);
  }
  
  return next(req);
};