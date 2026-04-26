import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';

interface Sede {
  id: string;
  name: string;
  address: string;
  phone: string;
  email: string | null;
  image: string;
}

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './contacto.component.html',
  styleUrl: './contacto.component.css'
})
export class ContactoComponent {
  sedes: Sede[] = [
    { id: 'santiago',    name: 'Santiago',    address: "Avenida Libertador Bernardo O'Higgins 100, Santiago", phone: '+56298765432', email: 'santiago@ejemplo.cl',     image: 'assets/img/sede-santiago.jpg'     },
    { id: 'valparaiso',  name: 'Valparaíso',  address: 'Condell 1500, Valparaíso',                            phone: '+56325432109', email: null,                       image: 'assets/img/sede-valparaiso.jpg'   },
    { id: 'vinadelmar',  name: 'Viña del Mar', address: 'Calle Valparaíso 500, Viña del Mar',                 phone: '+56321098765', email: 'vina@ejemplo.cl',          image: 'assets/img/sede-vina.jpg'         },
    { id: 'puntaarenas', name: 'Punta Arenas', address: 'Avenida Colón 900, Punta Arenas',                    phone: '+56616789012', email: 'puntaarenas@ejemplo.cl',   image: 'assets/img/sede-puntaarenas.jpeg' },
    { id: 'copiapo',     name: 'Copiapó',      address: "Calle O'Higgins 700, Copiapó",                       phone: '+56523456789', email: 'atacama@ejemplo.cl',       image: 'assets/img/sede-copiapo.jpg'      },
  ];

  constructor(private router: Router) {}

  shortAddress(address: string): string {
    return address.split(',')[0].trim();
  }

  irAPedirTurno(): void {
    this.router.navigate(['/pedir-turno']);
  }
}
