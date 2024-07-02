let autoScrollMap = new Map();

function setScrollMap(){
    console.log("set Auto Scroll")
    let scrollElement = document.getElementsByClassName("scroll");
    console.log(scrollElement);
    for(var i = 0; i < scrollElement.length; i++){
        autoScrollMap.set(scrollElement[i],"down");
        scrollTable(scrollElement[i]);        
    }


}

function scrollTable(target) {  
    setInterval(() => {
      if(currSec > 0){
        let direction = autoScrollMap.get(target);
        console.log(target,direction)
        if(direction=="down"){
            target.scrollTop = target.scrollTop + 2;
            if (target.offsetHeight + target.scrollTop >= target.scrollHeight) {
                autoScrollMap.set(target,"up");
            }
        }else{
            target.scrollTop = target.scrollTop - 2;
            if (target.offsetHeight + target.scrollTop <= target.offsetHeight) {
                autoScrollMap.set(target,"down");
            }
        }
      }
    }, 10);
    
}