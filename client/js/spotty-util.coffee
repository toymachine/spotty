#BASE_URL = "http://henk-desktop.hyveshq:8080"
BASE_URL = "http://spottyapi.appspot.com"
API_URL = BASE_URL + "/api"

getValue = (object, prop) ->
  if !(object && object[prop])
    return null
  if _.isFunction object[prop]
    object[prop]()
  else object[prop]

addSpottyHeader = (options) ->
  if window.spotifyUserID
    headers = if options.headers then options.headers else {}
    headers["X-SPOTTY-MEMBER-ID"] = window.spotifyUserID
    options.headers = headers

member_sync = (method, model, options) ->
    options.url = API_URL + model.url()
    if method is "read"
      options.url = options.url + model.get "id"

    addSpottyHeader(options)
    Backbone.sync method, model, options

list_sync = (method, model, options) ->
    #model is here the channellist
    options.url = API_URL + getValue(model, 'url')
    addSpottyHeader(options)
    Backbone.sync method, model, options