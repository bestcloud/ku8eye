# ku8eye 镜像批量导入压缩包的制作  
可以制作一个.tar.gz的压缩文件，里面存放多个镜像件，这样可以利用批量导入功能直接将多个镜像导入私库。下边将详细介绍如何制作压缩包文件（这里将以我们已经做好的压缩包‘ku8-images.tar.gz’为例子进行讲解，已经做好的gz文件存放在云盘的Value-packages/ku8_ext_files/ku8_images下）。  
## 制作步骤  

#### 1.新建待压缩的文件夹（如‘ku8-images’），里边存放配置文件（如‘ku8-images.json’）和镜像文件夹（如‘jre8’）。  
#### 2.在待压缩文件夹下创建镜像文件夹（如‘jre8’），里边存放本镜像文件（如jre8.img，必须为.img或者.tar后缀）、logo图片（如‘java.png’，图片限制尺寸110*40，高度必为40，宽度小于110）和Dockerfile等文件。  
#### 3.如2步骤添加其他文件夹和相关文件。  
#### 4.创建json配置文件，配置文件的名字必须为文件夹名加‘.json’组成（如ku8-images.json）  
json文件格式如下，这里只列举一个jre8例子  
```xml

1.新建待压缩的文件夹（如‘ku8-images’），里边存放配置文件（如‘ku8-images.json’）和镜像文件夹（如‘jre8’）。  
2.在待压缩文件夹下创建镜像文件夹（如‘jre8’），里边存放本镜像文件（如jre8.img，必须为.img或者.tar后缀）、logo图片（如‘java.png’，图片限制尺寸110*40，高度必为40，宽度小于110）和Dockerfile等文件。  
3.如2步骤添加其他文件夹和相关文件。  
4.创建json配置文件，配置文件的名字必须为文件夹名加‘.json’组成（如ku8-images.json）  
json文件格式如下，这里只列举一个jre8例子  

{
"imageShell":[{
"path":"jre8" , 
"saveImageName":"jre8.img",
"image":{"title":"Jre 8" , 
"imageName":"jre8", 
"version":"2015-01-07",
"versionType":"1",
"publicImage":"0",
"size":346660,
"category":"middleware",
"imageIconUrl":"java.png", 
"status":"0", 
"buildFile":"Dockerfile",
"autoBuildCommand":"",
"autoBuild":"0",
"note":"auto imported"
}

}]
}  
```
各个字段说明：  
**imageShell**：最外层标识，必有字段  
**path**：镜像文件所在文件夹名字，必有字段  
**saveImageName**：保存的镜像文件名，必有字段  
**image**：镜像信息标识，必有字段  
**title**：镜像前台显示的标志，必有字段  
**imageName**：镜像名字（存储到私时的镜像名字），必有字段  
**version**：版本（tag名字），必有字段  
**versionType**：版本类型，0开发版，1生产版，不填默认为0  
**publicImage**：是否为共有镜像，1为共有镜像，0为私有镜像，不填默认为0  
**size**：镜像文件大小，单位为KB，不填默认为0  
**category**：镜像种类（如middleware，database，other等），必有字段  
**imageIconUrl**：提供的logo名字  
**status**：镜像状态，0为可用，1为不可用，不填默认为0  
**buildFile**：直接填写Dockerfile的名字，如果没有可以置空  
**autoBuildCommand**：自动构建命令，暂时此字段保留置空即可  
**autoBuild**：是否自动构建，0为不是，1为是，暂时此字段保留置空即可，不填默认为0  
**note**：备注  
#### 5.打包并压缩文件，将待压缩的文件夹（如‘ku8-images’）打成tar包并压缩成gz结尾的文件，最终生成‘.tar.gz’结尾压缩文件（如ku8-images.tar.gz）  

}
]
}  
各个字段说明：  
imageShell：最外层标识，必有字段  
path：镜像文件所在文件夹名字，必有字段  
saveImageName：保存的镜像文件名，必有字段  
image：镜像信息标识，必有字段  
title：镜像前台显示的标志，必有字段  
imageName：镜像名字（存储到私时的镜像名字），必有字段  
version：版本（tag名字），必有字段  
versionType：版本类型，0开发版，1生产版，不填默认为0  
publicImage：是否为共有镜像，1为共有镜像，0为私有镜像，不填默认为0  
size：镜像文件大小，单位为KB，不填默认为0  
category：镜像种类（如middleware，database，other等），必有字段  
imageIconUrl：提供的logo名字  
status：镜像状态，0为可用，1为不可用，不填默认为0  
buildFile：直接填写Dockerfile的名字，如果没有可以置空  
autoBuildCommand：自动构建命令，暂时此字段保留置空即可  
autoBuild：是否自动构建，0为不是，1为是，暂时此字段保留置空即可，不填默认为0  
note：备注  
5.打包并压缩文件，将待压缩的文件夹（如‘ku8-images’）打成tar包并压缩成gz结尾的文件，最终生成‘.tar.gz’结尾压缩文件（如ku8-images.tar.gz）  

至此压缩包制作完成。
