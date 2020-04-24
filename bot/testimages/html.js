var page = require('webpage').create();
page.viewportSize = {
	width: 900,
	height: 900
	};
page.open('file:///C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/album.html', function() {
  page.render('C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/image.jpg');
  phantom.exit();
});