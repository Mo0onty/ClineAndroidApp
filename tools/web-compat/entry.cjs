// Web Compatibility Entry Point for Cline Android
// This module provides polyfills and compatibility fixes for the web runtime

const WebCompat = {
    version: "1.0.0",
    name: "Cline Web Compatibility Layer",
    
    // Browser detection
    isAndroid: typeof navigator !== 'undefined' && /Android/i.test(navigator.userAgent),
    isMobile: typeof navigator !== 'undefined' && /Mobi|Android/i.test(navigator.userAgent),
    
    // Polyfills
    polyfills: {
        // Fetch polyfill for older browsers
        fetch: function() {
            if (typeof window !== 'undefined' && !window.fetch) {
                // Simple fetch polyfill
                window.fetch = function(url, options = {}) {
                    return new Promise((resolve, reject) => {
                        const xhr = new XMLHttpRequest();
                        xhr.open(options.method || 'GET', url);
                        
                        if (options.headers) {
                            Object.entries(options.headers).forEach(([key, value]) => {
                                xhr.setRequestHeader(key, value);
                            });
                        }
                        
                        xhr.onload = () => {
                            if (xhr.status >= 200 && xhr.status < 300) {
                                resolve({
                                    ok: true,
                                    status: xhr.status,
                                    statusText: xhr.statusText,
                                    json: () => JSON.parse(xhr.responseText),
                                    text: () => xhr.responseText
                                });
                            } else {
                                reject(new Error(`HTTP error! status: ${xhr.status}`));
                            }
                        };
                        
                        xhr.onerror = () => reject(new Error('Network error'));
                        xhr.send(options.body || null);
                    });
                };
            }
        },
        
        // Promise polyfill
        promise: function() {
            if (typeof Promise === 'undefined') {
                // Minimal Promise polyfill
                window.Promise = function(executor) {
                    let resolve, reject;
                    const promise = {
                        then: function(onFulfilled, onRejected) {
                            return this;
                        },
                        catch: function(onRejected) {
                            return this;
                        }
                    };
                    
                    executor(function(value) { resolve(value); }, function(reason) { reject(reason); });
                    return promise;
                };
            }
        },
        
        // Object.assign polyfill
        objectAssign: function() {
            if (typeof Object.assign !== 'function') {
                Object.assign = function(target) {
                    for (let i = 1; i < arguments.length; i++) {
                        const source = arguments[i];
                        for (const key in source) {
                            if (Object.prototype.hasOwnProperty.call(source, key)) {
                                target[key] = source[key];
                            }
                        }
                    }
                    return target;
                };
            }
        }
    },
    
    // Storage utilities
    storage: {
        getItem: function(key) {
            try {
                return localStorage.getItem(key);
            } catch (e) {
                return null;
            }
        },
        
        setItem: function(key, value) {
            try {
                localStorage.setItem(key, value);
                return true;
            } catch (e) {
                return false;
            }
        },
        
        removeItem: function(key) {
            try {
                localStorage.removeItem(key);
                return true;
            } catch (e) {
                return false;
            }
        }
    },
    
    // Network utilities
    network: {
        isOnline: function() {
            return typeof navigator !== 'undefined' && navigator.onLine;
        },
        
        getConnectionType: function() {
            if (typeof navigator !== 'undefined' && navigator.connection) {
                return navigator.connection.effectiveType || 'unknown';
            }
            return 'unknown';
        }
    },
    
    // Device utilities
    device: {
        getPlatform: function() {
            if (typeof navigator !== 'undefined') {
                if (/Android/i.test(navigator.userAgent)) return 'android';
                if (/iPhone|iPad|iPod/i.test(navigator.userAgent)) return 'ios';
                if (/Windows/i.test(navigator.userAgent)) return 'windows';
                if (/Macintosh|Mac OS X/i.test(navigator.userAgent)) return 'mac';
                if (/Linux/i.test(navigator.userAgent)) return 'linux';
            }
            return 'unknown';
        },
        
        getUserAgent: function() {
            return typeof navigator !== 'undefined' ? navigator.userAgent : 'unknown';
        }
    },
    
    // Initialize all polyfills
    init: function() {
        this.polyfills.fetch();
        this.polyfills.promise();
        this.polyfills.objectAssign();
        
        console.log('Web Compatibility Layer initialized');
    }
};

// Initialize on load
if (typeof window !== 'undefined') {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => WebCompat.init());
    } else {
        WebCompat.init();
    }
}

// Export for CommonJS
if (typeof module !== 'undefined' && module.exports) {
    module.exports = WebCompat;
}
