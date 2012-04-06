getValue = (object, prop) ->
  if !(object && object[prop])
    return null
  if _.isFunction object[prop]
    object[prop]()
  else object[prop]

list_sync = (method, model, options) ->
    #model is here the channellist
    options.url = "http://127.0.0.1:8080/api" + getValue(model, 'url')
    Backbone.sync method, model, options