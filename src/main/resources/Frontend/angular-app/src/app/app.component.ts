import { templateSourceUrl } from '@angular/compiler';
import { Component, OnInit} from '@angular/core';
import { ButtonListener } from 'src/assets/js/ButtonFunction'

declare var buttonListeners: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'angular-app'

  ngOnInit(): void {
    var buttonFunction : ButtonListener = new ButtonListener;

    buttonFunction.buttonListeners();
  }
}
