sp = getSpotifyApi 1
models = sp.require "sp://import/scripts/api/models"
views = sp.require "sp://import/scripts/api/views"
ui = sp.require "sp://import/scripts/ui"
session = models.session

templates =
  member_identify: '
    <H1>hell0 <%= id %></H1>
    <form class=".form-vertical">
      <label>Name</label>
      <input type="text" name="name"><br>
      <label>Email</label>
      <input type="text" name="email"><br>
      <button class="btn btn-success identity-save">submit</button>
    </form>'
  home: 'hello <%= name %>
    <div class="row">
      <div class="span2" id="channel-list">
      </div>
      <div class="span8" id="channel-container">
      </div>
    </div'
  channel_list_item: '<%= name %></a>'
  channel_item: '<h1>welcome to radio <%= name%></h1>
    <table class="track-list table-bordered table-striped">

    </table>
    <div>
      What do you think about this channel?<br>
      <textarea></textarea>
      <div class="chat-list">
      </div>
    </div>'

member_sync = (method, model, options) ->
    options.url = "http://127.0.0.1:8080/api" + model.url()
    if method is "read"
      options.url = options.url + model.get "id"
    Backbone.sync method, model, options

class Member extends Backbone.Model
  url: () ->
    base = "/member/"
  sync: member_sync
  initialize: () ->
      @set {id: session.anonymousUserID}

class MemberView extends Backbone.View
  initialize: () ->
    @render()
  render: () ->
    dict = @model.toJSON()
    @$el.html @template dict

class IdentifyView extends MemberView
  events:
    "click .identity-save": "save"
  save: (e) ->
    e.preventDefault()
    @model.save
      name: (@$el.find "input[name=name]").val()
      email: (@$el.find "input[name=email]").val()
  template: _.template templates.member_identify

class HomeView extends MemberView
  template: _.template templates.home

class ChannelListItemView extends Backbone.View
  initialize: () ->
    @render()
  events:
    "click": "selectModel"
  selectModel: () ->
    @model.set({selected: 1})
  template: _.template templates.channel_list_item
  render: () ->
    @$el.html @template @model.toJSON()
    @

class ChannelListView extends Backbone.View
  initialize: () ->
    @collection.on "add", @render, @
    @collection.on "reset", @renderList, @
    @collection.on "change:selected", @showChannel, @
    @collection.fetch()
  channelItemElement: false
  renderList: (list) ->
    @$el.children().remove()
    list.each (model) ->
      @render(model)
    , @
  showChannel: (model) ->
    channelItemView = new ChannelItemView({model: model, el: @channelItemElement})
    channelItemView.render()
    model.set {selected: 0}, {silent: true}
  render: (model) ->
    channelItemView = new ChannelListItemView {model: model}
    element = channelItemView.render().el
    @$el.append element

class Channel extends Backbone.Model
  initialize: () ->
    @tracks = new TrackList({channel: @})

class ChannelItemView extends Backbone.View
  template:  _.template templates.channel_item
  render: () ->
    @$el.html @template @model.toJSON()
    trackListView = new TrackListView({collection: @model.tracks, el: @$el.find ".track-list"})
    chatMessages = new ChatMessages {channel: @model}
    chatMessagesView = new ChatMessagesView {collection: chatMessages, el: @$el.find ".chat-list"}

class ChannelList extends Backbone.Collection
  url: "/channels"
  model: Channel
  sync: list_sync

class Track extends Backbone.Model

class TrackListView extends Backbone.View
  initialize: () ->
    @collection.on "reset", @render, @
    @collection.fetch()
  render: (tracks) ->
    iterator = _.bind (track) ->
      spotifyId = track.get "spotify-id"
      uri = "spotify:track:#{spotifyId}"
      spotifyTrack = models.Track.fromURI uri
      console.log spotifyTrack.name
      console.log @$el
      @$el.append spotifyTrack.name
    , @
    tracks.each iterator

class TrackList extends Backbone.Collection
  initialize: (options) ->
    @channel = options.channel
  model: Track
  url: () -> "/channel/" + @channel.get("id") + "/tracks"
  sync: list_sync

member = new Member()
member.fetch
  error: (object, response) ->
    identifyView = new IdentifyView {el: "#page-content", model: member}
  success: () ->
    if member.has "email"
      homeView = new HomeView {el: "#page-content", model: member}
      channelList = new ChannelList()
      channelListView = new ChannelListView {collection: channelList, el: "#channel-list"}
      channelListView.channelItemElement = "#channel-container"
    else
      console.error "cannot be here without email address"