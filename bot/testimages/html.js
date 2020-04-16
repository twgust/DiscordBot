var page = require('webpage').create();
page.viewportSize = {
	width: 2400,
	height: 2100
	};
page.open('file:///C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/album.html', function() {
  page.render('C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/image.jpg');
  phantom.exit();
});