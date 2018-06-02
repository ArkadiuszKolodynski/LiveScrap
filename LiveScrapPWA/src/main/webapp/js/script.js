$(document).ready(function() {
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker
                .register('service-worker.js')
                .then(function () {
                    console.log('Service Worker Registered');
                });
    }
    
    setInterval(function(){ $("#container").load(window.location.href + " #container"); }, 30000);
});