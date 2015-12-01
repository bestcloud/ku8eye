(function( $, undefined ) {

	var h = $(window).height(),
		w = $(window).width(),
		u = {
			'cluster': '/deploycluster/listtemplates',
		},
        par={},
		d = {},
		dailyDate = {};
	$.ajaxData = function( options ) {
		var defaults = {
			url: null,
			type: 'POST',
			data: {},
			dataType: 'json',
			onSuccess: function( data ){},
			onFail: function(){}
		};

		var settings = $.extend( {}, defaults, options );

		$.ajax({
            url: settings.url,
            type: settings.type,
            data: settings.data,
            dataType: settings.dataType,
            timeout: 120000
        })
        .done(function( data ){
            settings.onSuccess.call( this, data );
        })
        .fail(function(){
            settings.onFail.call( this );
        });
	};

    $.loadData = function (options) {
        var defaults = {
            onSuccess: function () {
            }
        };
		var settings = $.extend( {}, defaults, options ),
			dataCount = 0,
			loadStatus = true;
		$.each(u, function( i, item ) {
			$.ajaxData({
				url: item,
                timeout:15000,
				onSuccess: function( data ){
					 d[ i ] = data;
                     dataCount = dataCount + 1;
                     $('.load-img').html( dataCount * 100 + '%' );
                     if( dataCount == 1 ){
                         $( '#page' ).data('status', 'success');
                         settings.onSuccess.call( this );
                     }; 
				},
				onFail: function(){
					if( loadStatus == true ){
						loadStatus = false;
						$( '#page' ).data('status', 'error');
						settings.onFail.call( this );
					};
				}
			});
		});
	};

	$.dailyInit = function( options ) {
		var defaults = {};
		var settings = $.extend( {}, defaults, options );
//        var date = new Date();
//        dailyDate.y = date.getFullYear();
//        dailyDate.m = date.getMonth() + 1;
//        dailyDate.d = date.getDate();
//        dailyDate.date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
//        if( dailyDate.d<10 ){ dailyDate.d='0'+dailyDate.d };
//        if( dailyDate.m<10 ){ dailyDate.m='0'+dailyDate.m };
//        merger = dailyDate.y.toString() +"-"+ dailyDate.m.toString()+"-" + dailyDate.d.toString();
        $( '#page' ).pageControl({
            page: "cluster_main",
            loading: true,
            first: true
        });
	};

	$.fn.pageControl = function( options ){
		
		var defaults = {
			page: "cluster_main",
			loading: false,
			first: false
		};

		var settings = $.extend( {}, defaults, options ),
			elems = this;
		
		$.ajaxData({
			url: settings.page +'.html',
			type: 'GET',
			dataType: 'html',
			onSuccess: function( data ){
                $(elems).html(data).data('page', settings.page);
				switch( settings.page ){
					case "cluster_main":
						pageIndex( settings.loading, settings.first );
						break;
					case "mode":
						pageRate();
						break;
					case "indtallProgress":
						pageDetail();
						break;
                    case "testResult":
                        helpDetail();
                        break;
				};
			}
		});
		function pageIndex( loading, first ){
			
			var loadingString = '<div id="loadData"><div class="load-img">0%</div></div>';
            $( document ).on('click', '#reload', load);
          
            if (loading == true) {
                load();
            } else {
                render();
            }    
            function load() {
                $( '#errBox' ).detach();
                $( 'header' ).after( loadingString );
                $.loadData({
                    onSuccess: function(){
                        $( '#loadData' ).detach();
                        render();
                    },
                    onFail: function(){
                        $( '#loadData' ).detach();
                        render();
                   }
                });
            }
            function render(){
                if(d.cluster.length > 0 ){
					var count=1;
					var tableList="";
		            $.each(d.cluster,function(i,item){
		            	tableList += [
		                     count%2==1?'<tr>':'',
		                     count%2==1?'<th id="'+item.id+'"><ul class="Mode"><li><h3>'+item.name+'</h3></li><li class="mode_li">要求：最少'+item.minNodes+'节点,最大'+item.maxNodes+'节点</li><li class="mode_li">'+item.describe+'</li></ul></th>':'',
		                     count%2==0?'<th id="'+item.id+'"><ul><li><h3>'+item.name+'</h3></li><li class="mode_li">要求：最少'+item.minNodes+'节点,最大'+item.maxNodes+'节点</li><li class="mode_li">'+item.describe+'</li></ul></th>':'',
		                     count%2==0?'</tr>':''
		                ].join('');
		               
		                count++;
		            }); 
		           
		            $("#example1").html(tableList);  
		            
		            
		            $( '#example1' ).find('th').bind('click',function(){
//		            	par.id=$( this ).attr('id');
		            	$( '.content' ).pageControl({
							page: "mode"
						});

						$( '.content' ).data({
							'itemId': $( this ).attr('id')
						});
					});
		            
	            }
					
            };
            
		}; 
		
		
		function pageRate(){
			alert("第二页");
			var itemId = $( '.content' ).data( 'itemId' );
			$.ajax({
		        url:"/deploycluster/selecttemplate/"+itemId,
		        type: "GET",
		        dataType:"json",
		        success: function(data){
		        	$("#mode_h5").html("您选择了"+data.name+"模式，至少需要"+data.minNodes+"节点，建议"+data.maxNodes+"个节点");
		        	$.each(data.nodes,function(i,item){
		        		$("#example").append("<tr><td><input type='checkbox' value='"+item.hostId+"' class='chkbox' name='child' /></td><td>"+item.ip+"</td><td>"+item.hostName+"</td><td>"+item.hostName+"</td></tr>");
		        	});
		        },
		        onFail: function () {
		           alert("添加失败");
		        }
		    }); 
			
			//添加节点对话框
			$('#example1').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		                			return "<input type='checkbox' value='"+data+"' class='chkbox' name='child' />";
		            		} },
		        			{ "data": "hostName" },
		        			{ "data": "ip" },
		        			{ "data": "lastUpdated" },
		        			{ "data": "cores" },
		        			{ "data": "location" },
		        			
			       		],
		       		"buttons": [
		       		          {
		       		              text: 'Reload',
		       		              action: function ( e, dt, node, config ) {
		       		                  dt.ajax.reload();
		       		              }
		       		          }
		       		      ]
		    });
			
			
			$("#save").click(function(){
		    	alert("点击了按钮！");
		    	var de="";
		    	$(":checkbox:checked").closest("tr").find(":checkbox").each(function(i, eleDom) {
					// 遍历每个被选中的复选框所在行的文本框的值
					de += eleDom.value + ",";
				});
		    	alert("要添加的节点id"+de);
		    	
		    	$.ajax({
			        url:"/hostlist/"+de,
			        type: "GET",
			        dataType:"text",
			        success: function(data){
			        	alert("添加成功!!!="+data);
			        },
			        onFail: function () {
			           alert("添加失败");
			        }
			    }); 
			
				
		    });
			
			
			
			
		}
		
	};

})( jQuery );
