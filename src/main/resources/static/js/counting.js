let totalCount = 0;
let currCount = 0;

function addCount(){
    totalCount += 1;
    currCount += 1;
    displayCnt();
}

function instractCount(cnt){
    currCount += cnt;
    displayCnt();
}

function subtractCount(cnt){
    currCount -= cnt;
    displayCnt();
}

function displayCnt(){
    let totalStr = totalCount < 10 ? '0'+totalCount : ''+totalCount;
    let currStr = currCount < 10 ? '0'+currCount : ''+currCount;
    let listTitle = "참여자("+currStr+"/"+totalStr+")";

    $("#listTitle").text(listTitle);
}