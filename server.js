var express = require('express');
var path = require('path');
var server = express.createServer();
server.configure(function(){
    server.use(express.static(path.join(__dirname, "resources/public")));
});
server.listen(8002);
