let autoScrollMap = new Map();
let scrollTopMap = new Map();

function setScrollMap(){
    //console.log("set Auto Scroll")
    let scrollElement = document.getElementsByClassName("scroll");
    //console.log(scrollElement);
    for(var i = 0; i < scrollElement.length; i++){
        autoScrollMap.set(scrollElement[i],"down");
		scrollTopMap.set(scrollElement[i],scrollElement[i].scrollTop);
        scrollTable(scrollElement[i]);        
    }


}


function scrollTable(target) {
	let standardScrollTop = target.scrollTop;
    setInterval(() => {
        let direction = autoScrollMap.get(target);
        let prevScrollTop = scrollTopMap.get(target);
				//console.log(target,direction)
		if(target.scrollHeight > standardScrollTop+600){
			if(direction=="down"){
				target.scrollTop = target.scrollTop + 2;
				if(target.scrollTop != prevScrollTop){
					if (target.offsetHeight + target.scrollTop >= target.scrollHeight) {
						autoScrollMap.set(target,"up");
					}
				}else{
					direction = "up";
				}
				
			}else{
				target.scrollTop = target.scrollTop - 2;
				if(target.scrollTop != prevScrollTop){
					if (target.offsetHeight + target.scrollTop <= target.offsetHeight) {
						autoScrollMap.set(target,"down");
					}
				}else{
					direction = "down";
				}
			}
			scrollTopMap.set(target,target.scrollTop);
		}
        
		
    }, 50);
    
}