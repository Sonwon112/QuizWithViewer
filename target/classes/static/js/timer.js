currSec = 0;
const setTime = (second)=>{
    //stop();
    currSec = second;
    display(currSec);
}

let timerInterval;
const start = ()=>{
    $("#timer").css("color","black");
    timerInterval = setInterval(function(){
        currSec -= 1;
        if(currSec <= 0){
            stop();
            
        }
        display(currSec);
    },1000);
}
const stop = ()=>{
    $("#timer").css("color","red");
    clearInterval(timerInterval);
}
const reset =(second)=>{
    setTime(second);
    stop();
}

const display =(second)=>{
    $("#timer").text(second < 10 ? "0"+second : ""+second);
}