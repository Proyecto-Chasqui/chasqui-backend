 CKEDITOR.editorConfig = function(config) {
    config.resize_enabled = false;
    config.toolbar_Simple = [ [ 'Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-'] ];
    config.removePlugins = 'elementspath';
    config.readOnly = true;
};