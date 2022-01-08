import { templateSourceUrl } from '@angular/compiler';
import { stringify } from '@angular/compiler/src/util';
import { Component, OnInit} from '@angular/core';
import { ClassifierService } from './service/classifier.service';

declare var buttonListeners: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'angular-app'

  ngOnInit() {
    
  }

  /**
   * Classifier service used for calling classify method
   * upon start button click
   */
  private restClassifier: ClassifierService;

  /**
   * Activated upon clicking one of the datset buttons.
   * Sets current button to active and removes active from
   * all other buttons. Also changes file to be embedded in
   * the file preview box.
   * @param id 
   */
  dsClick(id : string) {

    var dsButtons = document.getElementsByClassName('datasetButton')

    for(var i = 0; i < dsButtons.length; i++) {
        dsButtons[i].className = dsButtons[0].className.replace(" active", "");
    }

    document.getElementById(id).className += " active";

    document.getElementById("embed").setAttribute("src", "https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/" + 
    document.getElementById(id).innerHTML.toLowerCase() + ".arff");
  }

  /**
   * Activated upon clicking one of the method buttons.
   * Sets current button to active and removes active from
   * all other buttons.
   * @param id 
   */
  mClick(id : string) {

    var mButtons = document.getElementsByClassName('methodButton');

    for(var i = 0; i < mButtons.length; i++) {
        mButtons[i].className = mButtons[0].className.replace(" active", "");
    }

    document.getElementById(id).className += " active";
  }

  /**
   * Button called upon clicking start button.
   * Calls classify method using currently selected 
   * dataset and method and outputs data to the
   * classifier output box.
   */
  startClick() {

    var mButtons = document.getElementsByClassName('methodButton active');
    var dsButtons = document.getElementsByClassName('datasetButton active')

    this.restClassifier.classify(dsButtons[0].innerHTML.toLowerCase(), mButtons[0].innerHTML.toLowerCase()).subscribe(
      res => {
        document.getElementById('outputContainer').innerHTML = res.output;
      });
  }
}