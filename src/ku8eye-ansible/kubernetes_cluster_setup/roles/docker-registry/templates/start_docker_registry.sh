docker run -d -p 5000:5000 --restart=always --name registry -v {{docker_registry_root_dir}}:/var/lib/registry {{docker_registry_image_id}}
