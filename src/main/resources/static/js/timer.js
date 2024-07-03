currSec = 0;
currState = "stop";

function startTimer(message){
    // console.log(message);
    let quizJSON = JSON.parse(message)
    let difficulty = quizJSON.difficulty === "아이스" ? "하" : quizJSON.difficulty;
    let second = difficulty === "상" ? 15 : 10;
    setTime(second);
    start();
}

const setTime = (second)=>{
    //stop();
    $("#timer").css("color","black");
    currSec = second;
    display(currSec);
}

let timerInterval;
const start = ()=>{
    currState="start";
    timerInterval = setInterval(function(){
        currSec -= 1;
        if(currSec <= 0){
            stop();
            
        }
        display(currSec);
    },1000);
}
const stop = ()=>{
    currState="stop";
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