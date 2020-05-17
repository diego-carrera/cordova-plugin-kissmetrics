const exec = require('cordova/exec');

exports.identify = (identity, s, f) => {
  if ( !identity ) {
    f('Please give an identity');
  } else {
    exec(s, f, 'KissmetricsPlugin', 'identify',  [identity]);
  }
};

exports.identity = (s, f) => {
  exec(s, f, 'KissmetricsPlugin', 'identity', []);
};

exports.setProperties = ( properties, s, f ) => {
  if ( !properties ) {
    f && f('No properties given');
    return;
  }
  if( properties && typeof properties !== 'object') {
    f && f('Properties is not an object')
    return;
  }
  exec(s, f, 'KissmetricsPlugin', 'setProperties', [properties]);
};

exports.setDistinct = ( propertyName, value, s, f ) => {
  if ( !propertyName && !value ) {
    f && f('Missing arguments');
  } else {
    exec(s, f, 'KissmetricsPlugin', 'setDistinct', [propertyName, value]);
  }
};

exports.record = (name, params, s, f) => {
  if ( !name ) {
    f && f('Missing parameter');
    return;
  }

  if ( params && typeof params !== 'object' ) {
    f && f('Object required');
    return;
  }

  if ( !params ) {
    exec(s, f, 'KissmetricsPlugin', 'record', [name])
  } else {
    exec(s, f, 'KissmetricsPlugin', 'record', [name, params])
  }

};
