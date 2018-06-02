var dataCacheName = 'WebscraperData';
var cacheName = 'Webscraper';

self.addEventListener('install', function (event) {
  console.log('Installing Service Worker...');
  event.waitUntil(
    caches.open(dataCacheName)
      .then(function (cache) {
        console.log('Precaching App Shell');
        cache.addAll([
                    '/',
                    'css/flag-icon.min.css',
                    'css/styles.css',
                    'images/ball.svg',
                    'images/icons/icon-72x72.png',
                    'images/icons/icon-96x96.png',
                    'images/icons/icon-128x128.png',
                    'images/icons/icon-144x144.png',
                    'images/icons/icon-152x152.png',
                    'images/icons/icon-192x192.png',
                    'images/icons/icon-384x384.png',
                    'images/icons/icon-512x512.png',
                    'js/script.js'
        ]);
      })
  );
});

self.addEventListener('activate', function (event) {
  console.log('Activating Service Worker...');
  event.waitUntil(
    caches.keys()
      .then(function (keyList) {
        return Promise.all(keyList.map(function (key) {
          if (key !== dataCacheName && key !== cacheName) {
            console.log('Removing old cache...', key);
            return caches.delete(key);
          }
        }));
      })
  );
  return self.clients.claim();
});

self.addEventListener('fetch', function(event) {
  event.respondWith(
    fetch(event.request)
      .then(function(res) {
        return caches.open(cacheName)
          .then(function(cache) {
            cache.put(event.request.url, res.clone());
            return res;
          });
      })
      .catch(function(err) {
          return caches.match(event.request);
      })
  );
});
