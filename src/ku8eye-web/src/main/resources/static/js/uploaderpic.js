function uploadpic() {
	
	var $ = jQuery,
    $list = $('#fileList'),
    // 优化retina, 在retina下这个值是2
    ratio = window.devicePixelRatio || 1,

    // 缩略图大小
    thumbnailWidth = 100 * ratio,
    thumbnailHeight = 100 * ratio,

    // Web Uploader实例
    uploader;
// 初始化Web Uploader
uploader = WebUploader.create({
    auto: true,
    swf: '../js/Uploader.swf',
    server: '/dockerimg/upload-picture',
    pick: '#filePicker',
    // 只允许选择文件，可选。
    accept: {
        title: 'Images',
        extensions: 'gif,jpg,jpeg,bmp,png',
        mimeTypes: 'image/*'
    }

});

uploader.on( 'uploadSuccess', function( file ) {
    $.ajax({
	    url:"/extresources/listlogosurl",
	    type: "GET",
	    dataType:"json",
	    success: function(data){
	    	var imageurl="";
	    	$.each(data,function(i,item){
	    		var _imageName=item.split("/");
	    		if(i==0){
	    			imageurl+="<image class='images' c='"+_imageName[3]+"'  style='border:1px solid red;width:10%;height:30px;margin:8px 0px;' src='/external/logo_pic/"+_imageName[3]+"'/>";
	    			$("#addImageUrl").html(_imageName[3]);
	    		}else{
	    			imageurl+="<image class='images' c='"+_imageName[3]+"'  style='width:10%;height:30px;margin:8px 0px;' src='/external/logo_pic/"+_imageName[3]+"'/>";	
	    		}
	    		
	    		//imageurl+="<option><image src='../img/success.png'/>werwer</option>";
	    	});
	    	$("#addImageUrlname").html(imageurl);
	    	
	    	$( '#addImageUrlname' ).find( '.images' ).click(function() {
	    		$('.images').css({'border':''});
	    		$(this).css({'border':'1px solid red'});
	    		$("#addImageUrl").html($( this ).attr('c'));
	    	});	    	
	    }
	});
});
}