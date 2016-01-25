function uploadimagesall(_path) {
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
    //server: '/dockerimg/upload-image',
    server: '/extresources/upload-file',
    // 选择文件的按钮。可选。
    // 内部根据当前运行是创建，可能是input元素，也可能是flash.
    pick: '#picker',
    formData:{
    	path:_path,
    	name:"zzzzzzzzz"
    },
    duplicate:true,
    sendAsBinary:true,
    extensions: 'img',
    auto:true
});

//uploader.options.formData.uid = $("#path").val();//全局参数
//局部参数
uploader.on('uploadBeforeSend', function(obj, data, headers) {
	var path_update=$("#path").val();
	data['path']=path_update;
});

//当有文件添加进来的时候
uploader.on( 'fileQueued', function( file ) {
    $list.append( '<div id="' + file.id + '" class="item">' +
        '<h4 class="info">' + file.name + '</h4>' +
        '<p class="state">等待上传...</p>' +
    '</div>' );
});

//文件上传过程中创建进度条实时显示。
uploader.on( 'uploadProgress', function( file, percentage ) {
	$('#myModal').modal({backdrop: 'static', keyboard: false});
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
    var path_update=$("#path").val();
    searchFile(path_update);
});

uploader.on( 'uploadError', function( file ) {
    $( '#'+file.id ).find('p.state').text('上传出错');
});
uploader.on( 'uploadComplete', function( file ) {
	$("#myModal").modal('hide'); 
    $( '#'+file.id ).find('.progress').fadeOut();
});

}