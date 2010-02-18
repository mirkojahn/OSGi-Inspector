package net.mjahn.inspector.core;

import org.osgi.framework.Version;

/**
 * Immutable VersionRange representation.
 * 
 * Defines a VersionRange as described in the OSGi specification. The VersionRange class
 * also accepts single Version numbers like "1.5.3.build20100220-1021" for instance. The
 * name "Rang", also missleading at first fits for this as well, because in OSGi, this
 * refers to the version specified and ANY upper version there is.
 *
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public final class VersionRange {

    Version lowerBound, upperBound, singleVersion = null;
    boolean withLB = true;
    boolean withUB = false;
    final String vRange;

    /**
     * Constructor expecting an OSGi Version range string. Please see the OSGi specification for 
     * further details on this one.
     * 
     * @since 1.0
     * @param range String representing an OSGi {@code org.osgi.framework.Version} or version range.
     */
    public VersionRange(String range) {
        // expect something like this [2.2.3,2.0) or 2.2.3
        vRange = range;
        // do a split to see what type we got
        String[] lowerAndUpper = range.split(",");
        // single version string
        if (lowerAndUpper.length == 1) {
        	singleVersion = new Version(lowerAndUpper[0]);
        	return;
        }

        // [1.2,5.0)
        else if (lowerAndUpper.length == 2) {
            setLowerBound(lowerAndUpper[0]);
            setUpperBound(lowerAndUpper[1]);
        } else {
            throw new IllegalArgumentException("Invalid Version Range syntax!");
        }
    }

    /**
     * Check if a given {@code org.osgi.framework.Version} is contained in this particular 
     * {@code net.mjahn.inspector.core.VersionRange}. This only works, if this class actually 
     * has an infinite upper boundary. Such kind of boundary only happens if the 
     * {@code net.mjahn.inspector.core.VersionRange} is defined as a single 
     * {@code org.osgi.framework.Version} number like "2.4.2".
     * 
     * @since 1.0
     * @param version the version to check if it is contained in this range.
     * @return true if it is within the range.
     */
    public boolean contains(Version version) {
        if (singleVersion != null) {
            if(isHigherVersion(version, singleVersion) || singleVersion.equals(version)){
            	return true;
            }
            return false;
        }
        // we define an upper bound, so this could never be true!
        return false;
    }
    
    /**
     * Check if a given {@code net.mjahn.inspector.core.VersionRange} is contained in this particular 
     * {@code net.mjahn.inspector.core.VersionRange}. In case there is no intersection, false is returned.
     * 
     * @since 1.0
     * @param range the range to check.
     * @return true if the VersionRange given is within this VersionRange instance.
     */
    public boolean contains(VersionRange range){
		if(range.singleVersion != null){
			// we got a single version delegate the decision
			return contains(range.singleVersion);
    	} else if(singleVersion != null){
    		// we define no upper boundary, so check if range has a lower number 
    		return isHigherVersion(range.lowerBound, singleVersion) || range.lowerBound.equals(singleVersion);
    	} else {
    		// we got two ranges let's do this slowly by exclusion
    		if(isHigherVersion(lowerBound, range.lowerBound)){
    			// our lower bound is higher... can't work
    			return false;
    		} else if(lowerBound.equals(range.lowerBound) && withLB == false){
    			// the lower bounds are equal, but the bound is not included
    			return false;
    		} else if(isHigherVersion(range.upperBound, upperBound)){
    			// the other upperBound version must be smaller
    			return false;
    		} else if(upperBound.equals(range.upperBound) && (withUB == false && range.withUB != false)){
    			return false;
    		} else {
    			// it must fit
    			return true;
    		}
    	}
    }
    
    /**
     * Treats the version as a number (not a range!) and checks if this version number is contained in the given
     * range. This method is good to check if an exported package version is satisfied by this range.
     * 
     * @since 1.0
     * @param version usually a package export version.
     * @return true if the version number is within the range specification.
     */
    public boolean isVersionInRange(Version version){
    	// single version range
    	if(singleVersion != null){
    		if(isHigherVersion(singleVersion, version)){
    			return false;
    		} else if (!singleVersion.equals(version)){
    			return false;
    		}
    	}
    	// case of a range
    	if(isHigherVersion(lowerBound, version) || (lowerBound.equals(version) && withLB == false)){
    		return false;
    	} else if(isHigherVersion(version, upperBound) || upperBound.equals(version) && withUB == false){
    		return false;
    	}
    	// in all other cases, we are within the range
    	return true;
    }
    
    /**
     * Get the intersecting version range or null if not intersecting at all.
     * 
     * @since 1.0
     * @param range the VersionRange to the check the intersection with. If range = null, 0.0.0 is assumed.
     * @return the intersecting VersionRange or null, if not intersecting at all.
     */
    public VersionRange getIntersectingRange(VersionRange range){
    	if(range == null){
    		range = new VersionRange("0.0.0");
    	}
    	if(contains(range)){
    		// cover if they are equal (ranges or versions) or range is smaller than this (if this is a version or a larger range)
    		return range;
    	} else if(range.contains(this)){
    		// covers if this is smaller than range (with range being a version or a larger range)
    		return this;
    	} else {
    		// now we really have to look for the boundaries
    		Version mLowerBoundary = null, mUpperBoundary = null;
    		String mWithLB = "", mWithUB = "";
    		if(singleVersion!=null){
    			// if we haven't caught it before, range defines a lower boundary
    			if(isHigherVersion(singleVersion,range.lowerBound) || singleVersion.equals(range.lowerBound)){
    				mLowerBoundary = singleVersion;
    				mWithLB = "[";
    				mUpperBoundary = range.upperBound;
    				mWithUB = (range.withUB?"]":")");
    			} else {
    				// must be the range
    				return range;
    			}
    		} else if (range.singleVersion != null){
    			// [(2343,456]) vs. 234.35.3 f.i.
    			if(isHigherVersion(lowerBound, range.singleVersion) || lowerBound.equals(range.singleVersion)){
    				return this;
    			} else {
    				mLowerBoundary = range.singleVersion;
    				mWithLB = "[";
    				mUpperBoundary = upperBound;
    				mWithUB = (withUB?"]":")");
    			}
    		} else {
    			// two potentially intersecting ranges
    			
    			// first check if they intersect at all
    			if(isHigherVersion(lowerBound, range.upperBound) || isHigherVersion(range.lowerBound, upperBound) ){
    				return null; 
    			} else if (upperBound.equals(range.lowerBound)) {
    				if(!withUB || !range.withLB){
    					return null;
    				} else {
    					return new VersionRange("["+upperBound+","+upperBound+"]");
    				}
    			} else if (range.upperBound.equals(lowerBound)) {
    				if(!range.withUB || !withLB){
    					return null;
    				} else {
    					return new VersionRange("["+lowerBound+","+lowerBound+"]");
    				}
    			}
    			
    			// check out the lower bound
    			if(isHigherVersion(lowerBound, range.lowerBound)){
    				mLowerBoundary = lowerBound;
    				mWithLB = (range.withLB?"[":"(");
    			} else if(isHigherVersion(lowerBound, range.lowerBound)){
    				mLowerBoundary = range.upperBound;
    				mWithLB = (withLB?"[":"(");
    			} else if (lowerBound.equals(range.lowerBound)){
    				// they are equal
    				mLowerBoundary = lowerBound;
    				if(!withLB || !range.withLB){
    					mWithLB = "(";
    				}else{
    					mWithLB = "[";
    				}
    			}
    			// check out the upper bound
    			if(isHigherVersion(upperBound,range.upperBound)){
    				mUpperBoundary = range.upperBound;
    				mWithUB = (range.withUB?"]":")");
    			} else if(isHigherVersion(range.upperBound,upperBound)){
    				mUpperBoundary = upperBound;
    				mWithUB = (range.withUB?"]":")");
    			} else if(upperBound.equals(range.upperBound)){
    				// they are equal
    				mUpperBoundary = upperBound;
    				if(!withUB || !range.withUB){
    					mWithUB = ")";
    				}else{
    					mWithUB = "]";
    				}
    			}
    			// that should be it.
    		}
    		return new VersionRange(mWithLB+mLowerBoundary.toString()+","+mUpperBoundary.toString()+mWithUB);
    	}
    	
    }

    /**
     * Prints the correct version String as defined in the OSGi spec.
     * 
     * @return String representation of this VersionRange. Could also be a single Version.
     * @since 1.0
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return vRange;
    }
    
    /**
     * Checks if the second version is smaller than the first version. The second version MUST be smaller.
     * Check if equal, if you want to cover that case as well.
     * 
     * @since 1.0
     * @param higherVersion the supposedly higher version.
     * @param lowerVersion the lower version.
     * @return
     */
    private boolean isHigherVersion(Version higherVersion, Version lowerVersion){
    	if(higherVersion.compareTo(lowerVersion)>0){
        	// from our version to infinity everything is included
        	return true;
        }
        return false;
    }
    
    private void setLowerBound(String lower) {
        if (lower.startsWith("(")) {
            lowerBound = new Version(lower.substring(1));
            withLB = false;
        } else {
        	// we have to have a "["
            lowerBound = new Version(lower.substring(1));
            withLB = true;
        } 
    }

    private void setUpperBound(String upper) {
        if (upper.endsWith(")")) {
            upperBound = new Version(upper.substring(0, upper.length() - 1));
            withUB = false;
        } else {
        	// we have to have a "]" bound
            upperBound = new Version(upper.substring(0, upper.length() - 1));
            withUB = true;
        } 
    }

    /**
     * Alternative implementation of the {@code java.lang.Object#equals(Object)} method.
     * 
     * @since 1.0
     * @param range the version range this should be compared to.
     * @return true if equal
     */
    public boolean equals(VersionRange range){
    	if(singleVersion != null ){
    		if(range.singleVersion != null){
    			// are both single version equal?
    			return singleVersion.equals(range.singleVersion);
    		}
    		// the other range is a "real" range... so not identical
    		return false;
    	} else if(lowerBound.equals(range.lowerBound) && withLB == range.withLB 
    			&& upperBound.equals(range.upperBound) && withUB == range.withUB){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Implementation of the {@code java.lang.Object#equals(Object)} method.
     * 
     * @since 1.0
     * @see java.lang.Object#equals(Object)
     * @param obj the object to compare to (only a VersionRange makes sense here)
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj){
    	if(obj instanceof VersionRange){
    		return equals((VersionRange)obj);
    	} else {
    		return false;
    	}
    }

    /**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	public String toJSON(){
		return "{\"versionRange\":\""+toString()+"\"}";
	}
}
