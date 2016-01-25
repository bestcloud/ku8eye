function uploadimages() {
    var $ = jQuery,
    $list = $('#thelist'),
    state = 'pending',
    uploader;
uploader = WebUploader.create({
	// 不压缩image
    resize: true,

    // swf文件路径
    swf: '../js/Uploader.swf',

    // 文件接收服务端。
    server: '/dockerimg/upload-image',

    // 选择文件的按钮。可选。
    // 内部根据当前运行是创建，可能是input元素，也可能是flash.
    pick: '#pickerImage',
    sendAsBinary:true,
    extensions: 'img,gz',
    auto:true
});



// 当有文件添加进来的时候
uploader.on( 'fileQueued', function( file ) {
//	$('#myModalMask').modal({backdrop: 'static', keyboard: false});
    $list.append( '<div id="' + file.id + '" class="item">' +
        '<h4 class="info">' + file.name + '</h4>' +
        '<p class="state">等待上传...</p>' +
    '</div>' );
});

// 文件上传过程中创建进度条实时显示。
uploader.on( 'uploadProgress', function( file, percentage ) {
    var $li = $( '#'+file.id ),
        $percent = $li.find('.progress .progress-bar');

    // 避免重复创建
    if ( !$percent.length ) {
        $percent = $('<div class="progress progress-striped active">' +
          '<div class="progress-bar" role="progressbar" style="width: 0%">' +
          '</div>' +
        '</div>').appendTo( $li ).find('.progress-bar');
    }

    $li.find('p.state').text('上传中');

    $percent.css( 'width', percentage * 100 + '%' );
});

uploader.on( 'uploadSuccess', function( file ) {
    $( '#'+file.id ).find('p.state').text('已上传');
    $.ajax({
	    url:"/extresources/listuploadedimages",
	    type: "GET",
	    dataType:"json",
	    success: function(data){
	    	var pathurl="";
	    	$.each(data,function(i,item){
	    		pathurl+="<option value="+item+">"+i+"</option>"
	    	});
	    	$("#addPathUrl").html(pathurl);
	    }
	});
});

uploader.on( 'uploadError', function( file ) {
    $( '#'+file.id ).find('p.state').text('上传出错');
});

uploader.on( 'uploadComplete', function( file ) {
//	$("#myModalMask").modal('hide'); 
    $( '#'+file.id ).find('.progress').fadeOut();
});

}