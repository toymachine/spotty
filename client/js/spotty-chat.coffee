class ChatConnection extends Backbone.Model
  sync: list_sync
  url: "/chat/token"
  initialize: () ->
    onOpen = ()->
      console.log "open chat channel"
    onMessage = () ->
      console.log "message", arguments
    onError = () ->
      console.log "error", arguments
    onClose = () ->
      console.log "closed"

    @fetch
      success: (chatConnection) ->
        token = chatConnection.get "token"
        return
        rtChannel = new goog.appengine.Channel token
        socket = rtChannel.open()
        socket.onopen = onOpen
        socket.onmessage = onMessage
        socket.onerror = onError
        socket.onclose = onClose

connection = new ChatConnection()

class ChatMessages extends Backbone.Collection
  sync: list_sync
  channel: false
  initialize: (options) ->
    @channel = options.channel
  url: () ->
    channelId = @channel.get("id")
    "/channel/#{channelId}/chat-messages"


class ChatMessagesView extends Backbone.View
  initialize: () ->
    @collection.on "reset", @renderList, @
    @collection.fetch()
  renderList: (list) ->
    list.each (message) ->
      console.log message
      @$el.append message
    , @