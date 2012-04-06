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