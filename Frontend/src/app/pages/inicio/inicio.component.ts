import { Component, OnDestroy, ViewChild, ElementRef, AfterViewInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent implements AfterViewInit, OnDestroy {
  @ViewChild('carousel') carouselRef!: ElementRef<HTMLDivElement>;
  mostrarBtnTop = false;
  private autoScrollId: any;
  private readonly GAP = 30;
  private readonly AUTO_SCROLL_INTERVAL = 3000;

  constructor(private router: Router) {}

  ngAfterViewInit(): void {
    this.startAutoScroll();
  }

  ngOnDestroy(): void {
    this.stopAutoScroll();
  }

  irAPedirTurno(): void {
    this.router.navigate(['/pedir-turno']);
  }

  irAServicios(): void {
    this.router.navigate(['/servicios']);
  }

  scrollLeft(): void {
    const carousel = this.carouselRef.nativeElement;
    const scrollAmount = this.getScrollAmount();
    if (carousel.scrollLeft <= 0) {
      carousel.scrollTo({ left: carousel.scrollWidth, behavior: 'smooth' });
    } else {
      carousel.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    }
  }

  scrollRight(): void {
    const carousel = this.carouselRef.nativeElement;
    const scrollAmount = this.getScrollAmount();
    if (carousel.scrollLeft + carousel.clientWidth + 1 >= carousel.scrollWidth) {
      carousel.scrollTo({ left: 0, behavior: 'smooth' });
    } else {
      carousel.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  }

  onCarouselEnter(): void { this.stopAutoScroll(); }
  onCarouselLeave(): void { this.startAutoScroll(); }

  private getScrollAmount(): number {
    const carousel = this.carouselRef?.nativeElement;
    if (!carousel) return 300;
    const card = carousel.querySelector('.servicio') as HTMLElement;
    return card ? card.offsetWidth + this.GAP : 300;
  }

  private startAutoScroll(): void {
    this.stopAutoScroll();
    this.autoScrollId = setInterval(() => { this.scrollRight(); }, this.AUTO_SCROLL_INTERVAL);
  }

  private stopAutoScroll(): void {
    clearInterval(this.autoScrollId);
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.mostrarBtnTop = window.scrollY > 200;
  }

  volverArriba(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
