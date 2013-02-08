/**
 * Created with IntelliJ IDEA.
 * User: Oleg.Rybak
 * Date: 05.02.13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */

BS.ResourceDialog = OO.extend(BS.AbstractModalDialog, {
  attachedToRoot: false,
  editMode: false,
  currentResourceName: "",
  existingResources: {},
  myData:{},
  getContainer: function() {
    return $('resourceDialog');
  },

  showDialog: function() {
    this.editMode = false;

    $j('#resource_type option').each(function() {
      var self = $j(this);
      self.prop("selected", self.val() == 'infinite');
    });
    $j('#resource_quota').val(1);
    $j('#customValues').val('');
    $j('#resource_name').val('');

    this.adjustDialogDisplay();
    this.showCentered();
    this.bindCtrlEnterHandler(this.submit.bind(this));
  },

  showEdit: function(resource_name) {
    this.editMode = true;
    this.currentResourceName = resource_name;
    $j('#resource_name').val(resource_name);
    var r = this.myData[resource_name]; // current resource contents
    var type = r['type'];
    if (r['infinite']) {
      type = "infinite"
    } else {
      type = type.toLowerCase();
    }
    $j('#resource_type option').each(function() {
      var self = $j(this);
      self.prop("selected", self.val() == type);
    });

    if (type === 'quoted') {
      $j('#resource_quota').val(r['quota']);
    } else if (type === 'custom') {
      $j('#resource_quota').val(1);
      $j('#customValues').val(r['customValues'].join('\n'));
    } else {
      $j('#resource_quota').val(1);
    }

    this.adjustDialogDisplay(this.editMode);
    this.showCentered();
    this.bindCtrlEnterHandler(this.submit.bind(this));
    BS.MultilineProperties.updateVisible();
  },

  adjustDialogDisplay: function(editMode) {
    if (editMode) {
      $j("#resourceDialogTitle").html('Edit Resource');
      $j("#resourceDialogSubmit").prop('value', 'Save');
    } else {
      $j("#resourceDialogTitle").html('Add Resource');
      $j("#resourceDialogSubmit").prop('value', 'Add Resource');
    }
    this.syncResourceSelectionState();
  },

  submit: function() {
    //if (!this.validate()) return false; // todo: enable validation
    this.close();
    if (this.editMode) {
      BS.SharedResourcesActions.editResource(this.currentResourceName);
    } else {
      BS.SharedResourcesActions.addResource();
    }
    return false;
  },

  clearErrors: function () {
    BS.Util.hide('error_Name');
    $j('#error_Name').html("");
    BS.Util.hide('error_Quota');
    $j('#error_Quota').html("");
  },

  validate: function() {
    var errorsPresent = false;
    this.clearErrors();
    // name changed
    var element = $j('#resource_name');
    var value = element.val().trim();
    if (value !== this.currentResourceName) {
      if (value.length === 0) { // check not empty
        BS.Util.show('error_Name');
        $j('#error_Name').html("Name must not be empty");
        errorsPresent = true;
      }
      if ((this.editMode && (this.currentResourceName !== value)) || (!this.editMode)) {
        if (this.existingResources[value]) { // check not used
          BS.Util.show('error_Name');
          $j('#error_Name').html("Name is already used");
          errorsPresent = true;
        }
      }
      element.val(value);
    }

    // todo: change quoted to type
    return !errorsPresent;
  },

  syncResourceSelectionState: function() {
    var flag = $j('#resource_type option:selected').val();
    if (flag === 'infinite') {
      this.toggleModeInfinite();
    } else if (flag === 'quoted') {
      this.toggleModeQuota();
    } else if (flag === 'custom') {
      this.toggleModeCustom();
    }
    BS.MultilineProperties.updateVisible();
  },

  toggleModeInfinite: function() {
    BS.Util.hide('quota_row');
    BS.Util.hide('custom_row');
  },

  toggleModeQuota: function() {
    BS.Util.show('quota_row');
    BS.Util.hide('custom_row');
  },

  toggleModeCustom: function() {
    BS.Util.hide('quota_row');
    BS.Util.show('custom_row');
  }
});