class ChatConnection extends Backbone.Model
  sync: list_sync
  url: "/chat/token"
  initialize: () ->
    onOpen = ()->
      console.log "open chat channel"
    onMessage = (arg) ->
      data = $.parseJSON(arg.data);
      msg = data["msg"]
      channelId = data["channel-id"]
      channelMessages = window.channel_messages[channelId]
      if msg and channelId and channelMessages
        chatMessage = new ChatMessage {msg: msg, channelId: channelId}
        channelMessages.add chatMessage
    onError = () ->
      console.log "error", arguments
    onClose = () ->
      console.log "closed"

    @fetch
      success: (chatConnection) ->
        token = chatConnection.get "token"
        rtChannel = new goog.appengine.Channel token
        socket = rtChannel.open()
        socket.onopen = onOpen
        socket.onmessage = onMessage
        socket.onerror = onError
        socket.onclose = onClose

class ChatMessages extends Backbone.Collection
  sync: list_sync
  channel: false
  initialize: (options) ->
    @channel = options.channel
    window.channel_messages = window.channel_messages || {}
    window.channel_messages[@channel.get('id')] = @
  url: () ->
    channelId = @channel.get("id")
    "/channel/#{channelId}/chat-messages"


class ChatMessage extends Backbone.Model
  sync: list_sync
  url: () ->
    channelId = @get("channelId")
    "/channel/#{channelId}/chat-message"

class ChatMessagesView extends Backbone.View
  initialize: () ->
    @collection.on "reset", @renderList, @
    @collection.on "add", @render, @
    @collection.fetch()
  render: (message) ->
    @$el.append "<tr>" +  "<td><img src='" + message.get('avatar') + "'></td>" + "<td>" + message.get('msg') + "</td>" + "</tr>"
  renderList: (list) ->
    list.each (message) ->
      @render message
    , @