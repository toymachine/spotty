var API_URL, BASE_URL, addSpottyHeader, getValue, list_sync, member_sync;

BASE_URL = "http://spottyapi.appspot.com";

API_URL = BASE_URL + "/api";

getValue = function(object, prop) {
  if (!(object && object[prop])) return null;
  if (_.isFunction(object[prop])) {
    return object[prop]();
  } else {
    return object[prop];
  }
};

addSpottyHeader = function(options) {
  var headers;
  if (window.spotifyUserID) {
    headers = options.headers ? options.headers : {};
    headers["X-SPOTTY-MEMBER-ID"] = window.spotifyUserID;
    return options.headers = headers;
  }
};

member_sync = function(method, model, options) {
  options.url = API_URL + model.url();
  if (method === "read") options.url = options.url + model.get("id");
  addSpottyHeader(options);
  return Backbone.sync(method, model, options);
};

list_sync = function(method, model, options) {
  options.url = API_URL + getValue(model, 'url');
  addSpottyHeader(options);
  return Backbone.sync(method, model, options);
};
