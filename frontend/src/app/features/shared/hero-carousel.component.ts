import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export interface HeroSlide {
  imagemUrl: string;
  titulo: string;
  subtitulo: string;
  botaoTexto?: string;
  botaoLink?: string;
}

@Component({
  selector: 'app-hero-carousel',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="hero-carousel" *ngIf="slides.length > 0">
      <!-- Container dos Slides -->
      <div class="hero-slides-container">
        <div 
          *ngFor="let slide of slides; let i = index" 
          class="hero-slide"
          [class.active]="i === currentIndex"
          [style.backgroundImage]="'url(' + slide.imagemUrl + ')'"
        >
          <div class="hero-overlay"></div>
          <div class="hero-content">
            <h1 class="hero-title">{{ slide.titulo }}</h1>
            <p class="hero-subtitle">{{ slide.subtitulo }}</p>
            <a 
              *ngIf="slide.botaoTexto && slide.botaoLink" 
              [routerLink]="slide.botaoLink" 
              class="hero-btn"
            >
              {{ slide.botaoTexto }}
            </a>
          </div>
        </div>
      </div>

      <!-- Setas de Navegação -->
      <button 
        *ngIf="slides.length > 1" 
        class="hero-nav prev" 
        (click)="previousSlide()"
        aria-label="Slide anterior"
      >
        ‹
      </button>
      <button 
        *ngIf="slides.length > 1" 
        class="hero-nav next" 
        (click)="nextSlide()"
        aria-label="Próximo slide"
      >
        ›
      </button>

      <!-- Indicadores (Dots) -->
      <div *ngIf="slides.length > 1" class="hero-dots">
        <span 
          *ngFor="let slide of slides; let i = index" 
          class="dot" 
          [class.active]="i === currentIndex"
          (click)="goToSlide(i)"
        ></span>
      </div>
    </div>
  `,
  styles: [`
    .hero-carousel {
      position: relative;
      width: 100%;
      height: 500px;
      overflow: hidden;
      margin-bottom: 3rem;
      border-radius: 12px;
      box-shadow: 0 8px 30px rgba(0,0,0,0.1);
    }

    .hero-slides-container {
      position: relative;
      width: 100%;
      height: 100%;
    }

    .hero-slide {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-size: cover;
      background-position: center;
      opacity: 0;
      transition: opacity 0.8s ease-in-out;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .hero-slide.active {
      opacity: 1;
      z-index: 1;
    }

    .hero-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.4);
      z-index: 1;
    }

    .hero-content {
      position: relative;
      z-index: 2;
      text-align: center;
      color: white;
      padding: 2rem;
      max-width: 800px;
      animation: fadeInUp 0.8s ease;
    }

    .hero-title {
      font-size: 3rem;
      font-weight: 800;
      margin: 0 0 1rem 0;
      text-shadow: 2px 2px 8px rgba(0,0,0,0.5);
      letter-spacing: -1px;
    }

    .hero-subtitle {
      font-size: 1.4rem;
      margin-bottom: 2rem;
      text-shadow: 1px 1px 4px rgba(0,0,0,0.5);
      font-weight: 400;
    }

    .hero-btn {
      display: inline-block;
      padding: 1rem 2.5rem;
      background: #111;
      color: white;
      text-decoration: none;
      border-radius: 30px;
      font-weight: 700;
      font-size: 1.1rem;
      transition: all 0.3s;
      border: 2px solid #111;
    }

    .hero-btn:hover {
      background: transparent;
      color: white;
      border-color: white;
      transform: translateY(-2px);
    }

    .hero-nav {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      background: rgba(255,255,255,0.2);
      color: white;
      border: none;
      width: 50px;
      height: 50px;
      border-radius: 50%;
      font-size: 2rem;
      cursor: pointer;
      z-index: 10;
      transition: all 0.3s;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .hero-nav:hover {
      background: rgba(255,255,255,0.4);
    }

    .hero-nav.prev { left: 20px; }
    .hero-nav.next { right: 20px; }

    .hero-dots {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 10px;
      z-index: 10;
    }

    .dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: rgba(255,255,255,0.5);
      cursor: pointer;
      transition: all 0.3s;
    }

    .dot.active {
      background: white;
      width: 30px;
      border-radius: 6px;
    }

    .dot:hover {
      background: rgba(255,255,255,0.8);
    }

    @keyframes fadeInUp {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    /* Responsivo */
    @media (max-width: 768px) {
      .hero-carousel {
        height: 400px;
      }
      .hero-title {
        font-size: 2rem;
      }
      .hero-subtitle {
        font-size: 1.1rem;
      }
      .hero-nav {
        width: 40px;
        height: 40px;
        font-size: 1.5rem;
      }
    }
  `]
})
export class HeroCarouselComponent implements OnInit, OnDestroy {
  currentIndex = 0;
  private intervalId: any;

  // Configuração dos banners (pode vir de um serviço futuramente)
  slides: HeroSlide[] = [
    {
      imagemUrl: 'https://images.unsplash.com/photo-1556906781-9a412961c28c?w=1200',
      titulo: 'Nova Coleção 2026',
      subtitulo: 'Estilo e conforto para o seu dia a dia',
      botaoTexto: 'Ver Produtos',
      botaoLink: '/produtos'
    },
    {
      imagemUrl: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200',
      titulo: 'Promoção de Verão',
      subtitulo: 'Até 50% de desconto em produtos selecionados',
      botaoTexto: 'Aproveitar',
      botaoLink: '/produtos'
    },
    {
      imagemUrl: 'https://images.unsplash.com/photo-1472851294608-4151713487e1?w=1200',
      titulo: 'Frete Grátis',
      subtitulo: 'Em compras acima de R$ 260,00',
      botaoTexto: 'Comprar Agora',
      botaoLink: '/produtos'
    }
  ];

  ngOnInit(): void {
    this.startAutoPlay();
  }

  ngOnDestroy(): void {
    this.stopAutoPlay();
  }

  startAutoPlay(): void {
    // Muda de slide automaticamente a cada 5 segundos
    this.intervalId = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  stopAutoPlay(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  nextSlide(): void {
    this.currentIndex = (this.currentIndex + 1) % this.slides.length;
  }

  previousSlide(): void {
    this.currentIndex = (this.currentIndex - 1 + this.slides.length) % this.slides.length;
  }

  goToSlide(index: number): void {
    this.currentIndex = index;
    // Reinicia o autoplay quando o usuário clica manualmente
    this.stopAutoPlay();
    this.startAutoPlay();
  }
}
