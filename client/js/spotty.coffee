sp = getSpotifyApi 1
models = sp.require "sp://import/scripts/api/models"
views = sp.require "sp://import/scripts/api/views"
ui = sp.require "sp://import/scripts/ui"
session = models.session

getValue = (object, prop) ->
  if !(object && object[prop])
    return null
  if _.isFunction object[prop]
    object[prop]()
  else object[prop]


templates =
  member_identify: '
    hell0 <%= id %>
    <form>
      <input type="text" name="name"><br>
      <input type="text" name="email"><br>
      <button class="identity-save">submit</button>
    </form>'
  home: 'hello <%= name %>
    <div id="channel-list">
    </div>
    <div id="channel-container">
    </div>'
  channel_list_item: '<%= name %></a>'
  channel_item: '<h1>welcome to radio <%= name%></h1>
    <div class="track-list"></div>
    <div>
      What do you think about this song?<br>
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

list_sync = (method, model, options) ->
    #model is here the channellist
    options.url = "http://127.0.0.1:8080/api" + getValue(model, 'url')
    Backbone.sync method, model, options

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
    #console.log(@$el)
    playlist = new models.Playlist()
    playlistView = new views.List playlist, (track) ->
      console.log track
      return new views.Track(track, views.Track.FIELD.NAME)

    tracks.each (track) ->
      spotifyId = track.get "spotify-id"
      console.log playlist.add "spotify:track:#{spotifyId}"

    playlist.add "spotify:track:4Jv7lweGIUOFQ7Oq2AtAh9"
    #console.log "playlist", playlist

    console.log "pView", playlistView.node
    @$el.append playlistView.node

class TrackList extends Backbone.Collection
  initialize: (options) ->
    @channel = options.channel
  model: Track
  url: () -> "/channel/" + @channel.get("id") + "/tracks"
  sync: list_sync

member = new Member()
member.fetch
  error: (object, response) ->
    #if response status = 404
    identifyView = new IdentifyView {el: "#page-content", model: member}
  success: () ->
    if member.has "email"
      homeView = new HomeView {el: "#page-content", model: member}
      channelList = new ChannelList()
      channelListView = new ChannelListView {collection: channelList, el: "#channel-list"}
      channelListView.channelItemElement = "#channel-container"
    else
      console.error "cannot be here without email address"