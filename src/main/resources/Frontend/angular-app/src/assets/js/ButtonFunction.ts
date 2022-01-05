import { assertNotNull } from '@angular/compiler/src/output/output_ast';
import { SocketService } from 'src/app/services/socket.service';
import { Socket } from 'ngx-socket-io';

export class ButtonListener {
    buttonListeners() {

        var dsButtons = document.getElementsByClassName('datasetButton')
        var mButtons = document.getElementsByClassName('methodButton');

        console.log("here");
        for (var i = 0; i < dsButtons.length; i++) {
            var dsActive = dsButtons[i];
            dsButtons[i].addEventListener("click", dsClick);
        }

        for (var i = 0; i < mButtons.length; i++) {
            var mActive = mButtons[i];
            mButtons[i].addEventListener("click", mClick);
        }


        document.getElementById('StartButton').addEventListener("click", function() {

            var dataset, classifier;

            for(var i = 0; i < dsButtons.length; i++) {
                if(dsButtons[i].className == 'active') {
                    dataset = dsButtons[i].innerHTML;
                }
            }

            for(var i = 0; i < mButtons.length; i++) {
                if(mButtons[i].className == 'active') {
                    classifier = mButtons[i].innerHTML;
                }
            }

            var output = dataset + classifier;

            var socket : Socket = new Socket()

            var socketService : SocketService = new SocketService;

            socket.outputInfo(output);
        });
    }
}

function dsClick(this : HTMLElement) {

    var dsButtons = document.getElementsByClassName('datasetButton')

    for(var i = 0; i < dsButtons.length; i++) {
        dsButtons[i].className = dsButtons[0].className.replace(" active", "");
    }

    this.className += " active";
}

function mClick(this : HTMLElement) {

    var mButtons = document.getElementsByClassName('methodButton');

    for(var i = 0; i < mButtons.length; i++) {
        mButtons[i].className = mButtons[0].className.replace(" active", "");
    }

    this.className += " active";
}
