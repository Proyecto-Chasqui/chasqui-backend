 CKEDITOR.editorConfig = function(config) {
	     config.resize_enabled = false;
	     config.toolbar = 'Complex';
	     config.toolbar_Simple = [ [ 'Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink', '-', 'About' ] ];
	     config.toolbar_Complex = [
	             [ 'Bold', 'Italic', 'Underline','NumberedList', 'BulletedList', 'Strike', 'Subscript',
	                     'Superscript', '-', 'Cut', 'Copy',
	                     'Paste', 'Link', 'Unlink'],
	             [ 'Format', 'Font', 'Maximize'],
	             [ 'Undo', 'Redo', '-', 'JustifyLeft', 'JustifyCenter',
		                     'JustifyRight', 'JustifyBlock' ]];
};