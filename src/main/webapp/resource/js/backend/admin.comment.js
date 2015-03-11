zblog.register("zblog.comment");

zblog.comment.remove=function(commentid){
 $.ajax({
   type:"DELETE",
   url:"comments/"+commentid,
   dataType:"json",
   success:function(data){
	  if(data&&data.success){
	 //   window.location.reload();
    }else{
      alert(data.msg);
     }
   }
 });
}

zblog.comment.approve=function(commentid,status){
  $.ajax({
    type:"PUT",
    url:"comments/"+commentid,
    dataType:"json",
    data:{'status':status},
    success:function(data){
     if(data&&data.success){
       window.location.reload();
     }else{
       alert(data.msg);
      }
    }
  });
 }