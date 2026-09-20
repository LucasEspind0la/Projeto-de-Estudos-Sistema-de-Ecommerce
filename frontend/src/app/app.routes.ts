import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { ProductsComponent } from './features/products/products.component';
import { CartComponent } from './features/cart/cart.component';
import { OrdersComponent } from './features/orders/orders.component';
import { OrderDetailsComponent } from './features/orders/order-details.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard.component';
import { AdminProductsComponent } from './features/admin/admin-products.component';
import { AdminProductFormComponent } from './features/admin/admin-product-form.component';
import { AdminCategoriesComponent } from './features/admin/admin-categories.component';
import { AdminCategoryFormComponent } from './features/admin/admin-category-form.component';
import { AdminOrdersComponent } from './features/admin/admin-orders.component'; // ✅ IMPORT DO COMPONENTE

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'produtos', component: ProductsComponent },
  { path: 'carrinho', component: CartComponent },
  { path: 'pedidos', component: OrdersComponent },
  { path: 'pedidos/:id', component: OrderDetailsComponent },
  { path: 'admin', redirectTo: '/admin/dashboard', pathMatch: 'full' },
  { path: 'admin/dashboard', component: AdminDashboardComponent },
  { path: 'admin/produtos', component: AdminProductsComponent },
  { path: 'admin/produtos/novo', component: AdminProductFormComponent },
  { path: 'admin/produtos/editar/:id', component: AdminProductFormComponent },
  { path: 'admin/categorias', component: AdminCategoriesComponent },
  { path: 'admin/categorias/nova', component: AdminCategoryFormComponent },
  { path: 'admin/categorias/editar/:id', component: AdminCategoryFormComponent },
  { path: 'admin/pedidos', component: AdminOrdersComponent }, 
  { path: '**', redirectTo: '/login' }
];