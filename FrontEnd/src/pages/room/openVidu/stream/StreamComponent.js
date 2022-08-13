import React, { Component } from "react";
import "./StreamComponent.css";
import OvVideoComponent from "./OvVideo";

// import MicOff from "@material-ui/icons/MicOff";
// import VideocamOff from "@material-ui/icons/VideocamOff";
// import VolumeUp from "@material-ui/icons/VolumeUp";
// import VolumeOff from "@material-ui/icons/VolumeOff";
// import FormControl from "@material-ui/core/FormControl";
// import Input from "@material-ui/core/Input";
// import InputLabel from "@material-ui/core/InputLabel";
// import IconButton from "@material-ui/core/IconButton";
// import HighlightOff from "@material-ui/icons/HighlightOff";
// import FormHelperText from "@material-ui/core/FormHelperText";

export default class StreamComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nickname: this.props.user.getNickname(),
      showForm: false,
      mutedSound: false,
      isFormValid: true,
    };
    this.handleChange = this.handleChange.bind(this);
    this.handlePressKey = this.handlePressKey.bind(this);
    this.toggleNicknameForm = this.toggleNicknameForm.bind(this);
    this.toggleSound = this.toggleSound.bind(this);
  }

  handleChange(event) {
    this.setState({ nickname: event.target.value });
    event.preventDefault();
  }

  toggleNicknameForm() {
    if (this.props.user.isLocal()) {
      this.setState({ showForm: !this.state.showForm });
    }
  }

  toggleSound() {
    this.setState({ mutedSound: !this.state.mutedSound });
  }

  handlePressKey(event) {
    if (event.key === "Enter") {
      console.log(this.state.nickname);
      if (this.state.nickname.length >= 3 && this.state.nickname.length <= 20) {
        this.props.handleNickname(this.state.nickname);
        this.toggleNicknameForm();
        this.setState({ isFormValid: true });
      } else {
        this.setState({ isFormValid: false });
      }
    }
  }

  render() {
    return (
      <div className="OT_widget-container">
        {/* <div className="pointer nickname">
          {this.state.showForm ? (
            <form id="nicknameForm">
              <buutton
                color="inherit"
                id="closeButton"
                onClick={this.toggleNicknameForm}
              >
                <p>HighlightOff</p>
                <HighlightOff />
              </buutton>
              <label htmlFor="name-simple" id="label">
                Nickname
              </label>
              <input
                color="inherit"
                id="input"
                value={this.state.nickname}
                onChange={this.handleChange}
                onKeyPress={this.handlePressKey}
                required
              />
              {!this.state.isFormValid && this.state.nickname.length <= 3 && (
                <p id="name-error-text">Nickname is too short!</p>
              )}
              {!this.state.isFormValid && this.state.nickname.length >= 20 && (
                <p id="name-error-text">Nickname is too long!</p>
              )}
            </form>
          ) : (
            <div onClick={this.toggleNicknameForm}>
              <span id="nickname">{this.props.user.getNickname()}</span>
              {this.props.user.isLocal() && <span id=""> (edit)</span>}
            </div>
          )}
        </div> */}

        {this.props.user !== undefined &&
        this.props.user.getStreamManager() !== undefined ? (
          <div className="streamComponent">
            <OvVideoComponent
              className="example"
              user={this.props.user}
              mutedSound={this.state.mutedSound}
            />

            {/* <div id="statusIcons">
              {!this.props.user.isVideoActive() ? (
                <div id="camIcon">
                  <VideocamOff id="statusCam" />
                  <p style={{ color: "white" }}>롸?</p>
                </div>
              ) : null}

              {!this.props.user.isAudioActive() ? (
                <div id="micIcon">
                  <p style={{ color: "white" }}>롸?</p>
                  <MicOff id="statusMic" />
                </div>
              ) : null}
            </div> */}
            <div>
              {!this.props.user.isLocal() && (
                <button id="volumeButton" onClick={this.toggleSound}>
                  {this.state.mutedSound ? (
                    <p>VolumeOff</p>
                  ) : (
                    // <VolumeOff color="secondary" />
                    <p>VolumeUp</p>
                    // <VolumeUp />
                  )}
                </button>
              )}
            </div>
          </div>
        ) : null}
      </div>
    );
  }
}
