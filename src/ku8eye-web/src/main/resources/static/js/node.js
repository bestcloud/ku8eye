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
					case "singleNode":
						pageRate();
						break;
					case "multiNode":
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
		                     count%2==1?'<th id="'+item.id+'" c="'+item.maxNodes+'"><ul class="Mode"><li><h3>'+item.name+'</h3></li><li class="mode_li">要求：最少'+item.minNodes+'节点,最大'+item.maxNodes+'节点</li><li class="mode_li">'+item.describe+'</li></ul></th>':'',
		                     count%2==0?'<th id="'+item.id+'" c="'+item.maxNodes+'"><ul><li><h3>'+item.name+'</h3></li><li class="mode_li">要求：最少'+item.minNodes+'节点,最大'+item.maxNodes+'节点</li><li class="mode_li">'+item.describe+'</li></ul></th>':'',
		                     count%2==0?'</tr>':''
		                ].join('');
		               
		                count++;
		            }); 
		           
		            $("#example1").html(tableList);  
		            
		            
		            $( '#example1' ).find('th').bind('click',function(){
		            	$( '.content' ).data({
							'itemId': $( this ).attr('id')
						});
		            	
		            	if($( this ).attr('c')==1){
		            		$( '.content' ).pageControl({
								page: "singleNode"
							});
		            	}else{
		            		$( '.content' ).pageControl({
								page: "multiNode"
							});
		            	}
		            	
					});
		            
	            }
					
            };
            
		}; 
		
		//单节点的js
		function pageRate(){
			var itemId = $( '.content' ).data( 'itemId' );
			//要添加的节点对话框的值
			$('#example1').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		                			return "<input type='radio' value='"+data+"' class='chkbox' name='child' checked='checked'/>";
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
			
			
			//模版默认的值显示
			$.ajax({
		        url:"/deploycluster/selecttemplate/"+itemId,
		        type: "GET",
		        dataType:"json",
		        success: function(data){
//		        	d.node=data;
		        	$("#mode_h5").html("您选择了"+data.name+"模式，至少需要"+data.minNodes+"节点，建议"+data.maxNodes+"个节点");

		        	$("#nodeIp").html(data.nodes[0].ip);
		        	$("#nodeName").html(data.nodes[0].hostName);
		        	
		        	$.each(data.nodes[0].nodeRoleParams,function(i,item){
		        		$.each(item,function(m,n){
		        			$("#example").append("<tr><td>"+i+"</td><td>"+n.name+"</td><td><input type='text' value='"+n.value+"'/></td><td>"+n.describe+"</td></tr>");
		        		});
		        	});
		        	
		        }
		    }); 
			
			//按保存，把数据添加到页面
			$("#save").click(function(){
				var td = $(".chkbox:checked").first().parent("td");
				td = $(td).next("td");
				var nodename = jQuery.trim($(td).text());
				td = $(td).next("td");
				var nodeip = jQuery.trim($(td).text());
				$("#nodeIp").html(nodeip);
	        	$("#nodeName").html(nodename);
			
		    });
			
			
			
			$("#nextStep").click(function(){
				alert("下一步");
				var de="";
				$(":checkbox:checked").closest("#example tr").find(":checkbox").each(function(i, eleDom) {
					// 遍历每个被选中的复选框所在行的文本框的值
					de += eleDom.value + ",";
				});
				
				$( '.content' ).data({
					'zoneId': de
				});
				
				$( '.content' ).pageControl({
					page: "modeNext"
				});
				
			});
			
//			$("#exect").click(function(){
//				if ($("#exect").attr("checked")) { 
//			        $(":checkbox").attr("checked", false);  
//			       
//			    } else {
//			        $(":checkbox").attr("checked", true);  
//			    }   
//			});
			
						
			

		}
		
		
		//多节点的js
		function pageDetail(){
			var itemId = $( '.content' ).data( 'itemId' );
			var li="";
			var node="";
			var arr=[];
			var count=0;
			$('#example1').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		                			return "<input type='checkbox' value='"+data+"' class='chkbox' name='child'/>";
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
			
			//模版默认的值显示
			$.ajax({
		        url:"/deploycluster/selecttemplate/"+itemId,
		        type: "GET",
		        dataType:"json",
		        success: function(data){
//		        	d.node=data;
		        	$("#mode_h5").html("您选择了"+data.name+"模式，至少需要"+data.minNodes+"节点，建议"+data.maxNodes+"个节点");
		        	$.each(data.nodes,function(i,item){
		        		arr[count]=item.hostName;
		        		count++;
		        	});
		        
	        		var str = []; 
	        		for(var i = 0;i < arr.length;i++){ 
	        		! RegExp(arr[i],"g").test(str.join(",")) && (str.push(arr[i])); 
	        		} 
	        		for(var j=0;j<str.length;j++){
	        			li += [
	    				       '<div style="padding: 10px 0px 7px 0px;">',
	    				       '<span>kubernetes:</span>',
	    				       '<span>'+str[j]+'</span>',
	    				       '</div>',

	    				       '<table id="'+str[j]+'" class="table table-bordered table-hover" style="font-size: 12px;">',
	    				       '<thead>',
	    				       '<tr>',
	    				       '<th>角色</th>',
	    				       '<th>参数名</th>',
	    				       '<th>参数值</th>',
	    				       '<th>描述</th>',
	    				       '</tr>',
	    	
	    				       '</thead>',
	    				       '</table >',
	    				       '<div style="padding: 15px 0px 7px 0px;">',
	    				       '<span >kubernetes:Nodes</span>',
	    				       '<a  class="btn btn-primary " data-toggle="modal" data-target="#myModal2">添加节点</a>',	
	    				       '</div>',
	    				       '<table id="'+str[j]+'0" class="table table-bordered table-hover" style="font-size: 12px;">',
	    				       '<thead>',
	    				       '<tr>',
	    				       '<th>节点</th>',
	    				       '<th>节点参数</th>',
	    				       '<th>操作</th>',
	    				       '</tr>',
	    				       '</thead>',
	    				       '</table>',
	    				       '<div style="margin-top:20px;border-top:1px solid;width:100%"></div>'   
	                       ].join('');
	        		}
	        		
	        		
	        		$("#multiNodes").html(li);
	        		$.each(data.nodes,function(i,item){
	        			
	        			$('table[id="'+item.hostName+'0"]').append("<tr><td>"+item.ip+"</td><td>"+item.hostName+"</td><td><span>设置</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span>删除</span></td></tr>");
	        			
	        			$.each(item.nodeRoleParams,function(m,n){
	        				$.each(n,function(a,b){
		        				$('table[id="'+item.hostName+'"]').append("<tr><td>"+item.hostName+"</td><td>"+b.name+"</td><td>"+b.value+"</td><td>"+b.describe+"</td></tr>");
		        				 
	        				});
	        				
	        			});
		        	});

		        }
		    }); 
			
			//按保存，把数据添加到页面
			$("#save").click(function(){
				
			
		    });
		
		}
//		function switchSysBar(){
//			alert("sdfsdfsdf");
//		}
		
	};

})( jQuery );
