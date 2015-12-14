(function( $, undefined ) {

	var h = $(window).height(),
		w = $(window).width(),
		u = {
			'cluster': '/deploycluster/listtemplates',
			'singleNode':'/nodelist/singleNode',
			'multiNode':'/nodelist/multiNode',
			'multiAddNode':'/nodelist/addNode',
			'addlist':'/addlist/1'
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
//					alert(i+"="+data);
					 d[ i ] = data;
                     dataCount = dataCount + 1;
                     $('.load-img').html( dataCount * 20 + '%' );
                     if( dataCount == 5 ){
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
                    case "installProgress":
                        progress();
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
			var root_passwd="";
			//要添加的节点对话框的值
			$('#example1').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		                			return "<input type='radio' value='"+data+"'  class='chkbox' name='child' checked='checked'/>";
		            		} },
		        			{ "data": "hostName" },
		        			{ "data": "ip" },
		        			{ "data": "rootPasswd" },
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

		        	$("#nodeIp").html(d.singleNode.ip);
		        	$("#nodeName").html(d.singleNode.hostName);
		        	
		        	$.each(d.singleNode.nodeRoleParams,function(i,item){
		        		var count=0;
		        		$.each(item,function(m,n){
		        			count++;
		        			$("#example").append("<tr><td>"+i+"</td><td>"+n.name+"</td><td><input type='text'  id='"+i+count+"' value='"+n.value+"'/></td><td>"+n.describe+"</td></tr>");
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
				td = $(td).next("td");
				root_passwd = jQuery.trim($(td).text());
				$("#nodeIp").html(nodeip);
	        	$("#nodeName").html(nodename);
	        	        	
		    });
			
			$("#nextStep").click(function(){
				
				var _templateString="";
		
	        	_templateString+=$("#nodeIp").html()+","+$("#nodeName").html()+","+root_passwd;
				$.each(d.singleNode.nodeRoleParams,function(i,item){
	        		$("#"+i+1).val();
	        		$("#"+i+2).val();
	        		_templateString+=","+i+","+$("#"+i+1).val()+","+$("#"+i+2).val();
	        	});
				
	        	$.ajax({
			        url:"/deploycluster/modifytemplate/0",
			        data:{
			        	'templateString':_templateString
			        },
			        type: "GET",
			        dataType:"text",
			        success: function(data){
			        	alert(data);
			        }
			    });
				
//				var de="";
//				$(":checkbox:checked").closest("#example tr").find(":checkbox").each(function(i, eleDom) {
//					// 遍历每个被选中的复选框所在行的文本框的值
//					de += eleDom.value + ",";
//				});
//				
//				$( '.content' ).data({
//					'zoneId': de
//				});
				
//				$( '.content' ).pageControl({
//					page: "installProgress"
//				});
				
			});
			

		}
		
		
		
		
		//多节点的js
		function pageDetail(){
			var itemId = $( '.content' ).data( 'itemId' );
			var root_passwd="";
			$('#example2').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		        				
		                			return "<input type='radio' value='"+data+"' class='chkbox' name='child' checked='checked'/>";
		            		} },
		        			{ "data": "hostName" },
		        			{ "data": "ip" },
		        			{ "data": "rootPasswd" },
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
			
			$('#example3').DataTable({
		        "ajax":"/addlist/1",
		        "columns": [
		        			{ "data": "id", render: function ( data, type, row ) {
		                		// Combine the first and last names into a single table field
		        				
		                			return "<input type='checkbox' value='"+data+"' class='chkbox' name='child' checked='checked'/>";
		            		} },
		        			{ "data": "hostName" },
		        			{ "data": "ip" },
		        			{ "data": "rootPasswd" },
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
			
			//选择节点按钮事件
			$("#selectSaveBtn").click(function(){
					var td = $(".chkbox:checked").first().parent("td");
					td = $(td).next("td");
					var nodename = jQuery.trim($(td).text());
					td = $(td).next("td");
					var nodeip = jQuery.trim($(td).text());
					td = $(td).next("td");
					root_passwd= jQuery.trim($(td).text());
					$("#multiIp").html(nodeip);
		        	$("#multiName").html(nodename);
		        	//模版默认的值显示
					$.ajax({
				        url:"/deploycluster/selecttemplate/"+itemId,
				        type: "GET",
				        dataType:"json",
				        success: function(data){
				        	$("#mode_h5").html("您选择了"+data.name+"模式，至少需要"+data.minNodes+"节点，建议"+data.maxNodes+"个节点");
				        }
				    });
					
				
					//Master的值
					var mastertable="";
					$.each(d.multiNode.nodeRoleParams,function(i,item){	
						var masterName="",mastervalue="",masterDescribe="";
		        		$.each(item,function(m,n){
		        		    masterName+=n.name+";";
		        			mastervalue+=n.value+";";
		        			masterDescribe+=n.describe+";";
		        		});
		        		mastertable+=[
		        			          '<tr>',
		        			          '<td>'+i+'</td>',
		        			          '<td>'+masterName+'</td>',
		        			          '<td>'+mastervalue+'</td>',
		        			          '<td>'+masterDescribe+'</td>',
		        			          '</tr>'
		        			          ].join('');
		        	});
					$("#multiMaster").html("<tr><th>角色</th><th>参数名</th><th>参数值</th><th>描述</th></tr>");
					$("#multiMaster").append(mastertable);
					
					//nodes的值
					var RoleParam="";
					$.each(d.multiAddNode.nodeRoleParams,function(a,b){ 
						$.each(b,function(c,d){
							RoleParam+=d.name+"="+d.value+";";
						});
		        		$("#multiNodes").append("<tr><td>"+nodeip+"</td><td>"+RoleParam+"</td><td><button data-toggle='modal' data-target='#myModal4' c='"+d.multiAddNode.ip+"' value='"+RoleParam+"' class='set'>设置</button>&nbsp;&nbsp;&nbsp;&nbsp;<button class='del'>删除</button></td></tr>");
		        	});
					//添加节点按钮事件
					$("#addSaveBtn").click(function(){
						var de="";
						$(":checkbox:checked").closest("#example3 tr").find(":checkbox").each(function(i, eleDom) {
							// 遍历每个被选中的复选框所在行的文本框的值
							de += eleDom.value + ",";
						});
						$.ajax({
					        url:"/getNode/"+de,
					        type: "GET",
					        dataType:"json",
					        success: function(data){
					        	var setcount=0;
					        	$.each(data,function(i,item){
					        	    RoleParam="";
					        		$.each(item.nodeRoleParams,function(m,n){
					        			
					        			$.each(n,function(m1,n1){
					        				RoleParam+=n1.name+"="+n1.value+";";
					        			});
					        		});
					        		setcount++;
					        		$("#multiNodes").append("<tr><td>"+item.ip+"</td><td id='set"+setcount+"'>"+RoleParam+"</td><td><button data-toggle='modal' data-target='#myModal4' count='set"+setcount+"' passwd='"+item.rootPassword+"' c='"+item.ip+"' value='"+RoleParam+"' class='set'>设置</button>&nbsp;&nbsp;&nbsp;&nbsp;<button class='del'>删除</button></td></tr>");
					        		
					        	});
					        	
					        	//修改参数
					        	$( '#multiNodes' ).find( 'tr .set' ).click(function() {
					        		$("#setting").html("");
									var dialogValue=$( this ).attr('value');
									var dialogip=$( this ).attr('c');
									var dialogpasswd=$( this ).attr('passwd');
									var modifyId=$( this ).attr('count');
									var arrname=dialogValue.split(";");
									var i;
									var li="";
									for(i=0;i<arrname.length-1;i++){
										var arrValue=arrname[i].split("=");
										li +=[
										      '<div class="form-group">',
										      '<label class="col-sm-2 control-label" >'+arrValue[0]+'</label>',
										      '<div class="col-sm-10">',
										      '<input type="text" class="form-control" value="'+arrValue[1]+'" id="'+arrValue[0]+'">',
										      '</div>',
										      '</div>'
										      ].join('');
									}
									$("#setting").html('<div class="form-group"><label class="col-sm-2 control-label">'+dialogip+dialogpasswd+'</label></div>'+li);
									
									$("#setSaveBtn").click(function(){
										$(this).unbind('click');
										$("#"+modifyId).html("ansible_ssh_user="+$("#ansible_ssh_user").val()+";ansible_ssh_pass="+$("#ansible_ssh_pass").val()+";");
									});
								});

					        	//删除行
								$( '#multiNodes' ).find( 'tr .del' ).click(function() {
									var j=this.parentNode.parentNode.rowIndex;
									document.getElementById('multiNodes').deleteRow(j);
								});

					        }  
					    });						
					});
 	
			});
			$("#nextStep").click(function(){
				
				var _templateString="";
	        	_templateString+=$("#multiIp").html()+","+$("#multiName").html()+","+root_passwd+";";

	        	$( '#multiNodes' ).find( 'td').each(function(i,item){
//	        		
	        	});
	        	
	        	
//				$.each(d.singleNode.nodeRoleParams,function(i,item){
//	        		$("#"+i+1).val();
//	        		$("#"+i+2).val();
//	        		_templateString+=","+i+","+$("#"+i+1).val()+","+$("#"+i+2).val();
//	        	});
				
	        	$.ajax({
			        url:"/deploycluster/modifytemplate/0",
			        data:{
			        	'templateString':_templateString
			        },
			        type: "GET",
			        dataType:"text",
			        success: function(data){
			        	alert(data);
			        }
			    });
			
				
//				$( '.content' ).pageControl({
//					page: "installProgress"
//				});
				
			});
			

		}
		
		function progress(){
			
		}
		
	};

})( jQuery );
