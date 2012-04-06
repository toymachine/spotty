sp = getSpotifyApi 1
models = sp.require "sp://import/scripts/api/models"

templates =
  channel_create: '<form class="well .form-vertical">
      <label>Name</label>
      <input type="text" name="name">
      <label>Description</label>
      <input type="text" name="description">
      <label>Image url</label>
      <input type="text" name="imageurl">
      <button class="btn btn-success identity-save">submit</button>
    </form>'
  channel_add_track: '<h1>Add tracks to  <%= name %></h1>
    <form class="well form-search">
      <input type="text" class="input-medium search-query">
      <button type="submit" class="btn">Search</button>
    </form>
    <div class="track-search-result"></div>'
  track_item: 'test'

class MyChannel extends Backbone.Model
    url: "/channel"
    sync: list_sync

class TrackSearchList extends Backbone.Collection
  model: TrackItem

class TrackSearchListView extends Backbone.View
  initialize: ()->
    @collection.on "reset", @render, @
  render: (list) ->
    console.log @$el
    list.each (track) ->
      trackItemView = new TrackItemView {model: track}
      @$el.append trackItemView.render()
    , @

class TrackItem extends Backbone.Model

class TrackItemView extends Backbone.View
  template: _.template templates.track_item
  render: () ->
    @$el.html @template @model.toJSON()

class ChannelAddTrackView extends Backbone.View
  initialize: () ->
    @render()
    @trackList = new TrackSearchList()
    @trackListView = new TrackSearchListView {collection: @trackList}
  events:
    "keyup .search-query": "search"
  search: (event) ->
    if event.keyCode isnt 13
      return

    query = ($ event.target).val()
    sp = getSpotifyApi 1
    models = sp.require "sp://import/scripts/api/models"
    search = new models.Search query
    search.localResults = models.LOCALSEARCHRESULTS.IGNORE
    tempList = []
    search.observe models.EVENT.CHANGE, ()->
      if search.tracks.length > 0
        $.each search.tracks, (index, track) ->
          track = new TrackItem JSON.stringify track
          tempList.push track
        console.log tempList
        @trackList.reset tempList
      else
        alert "not tracks found for #{query}"

    search.appendNext()
  template: _.template templates.channel_add_track
  render: () ->
    @$el.html @template @model.toJSON()

class ChannelCreateView extends Backbone.View
  initialize: () ->
    @render()
  template: _.template templates.channel_create
  render: () ->
    @$el.html @template
  events:
    "click button": "submit"
  submit: (e) ->
    e.preventDefault()
    form = @$el.find "form"
    data = {
      name: form.find("[name=name]").val()
      description: form.find("[name=description]").val()
      imageurl: form.find("[name=imageurl]").val()
    }
    console.log data
    channel = new MyChannel data
    channel.save()
    window.channelList.add(new Channel(channel.toJSON()))

    channelAddTrackView  = new ChannelAddTrackView {model: channel, el: @el}



