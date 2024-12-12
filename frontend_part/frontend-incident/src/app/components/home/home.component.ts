import {Component, OnInit} from '@angular/core';
import {
  Carousel,
  initTWE,
} from "tw-elements";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  ngOnInit(): void {
    initTWE({ Carousel });
  }

}
